package com.test.jenkins

import groovy.transform.InheritConstructors
import com.cloudbees.groovy.cps.NonCPS

@InheritConstructors
class DeployPipeline extends Pipeline {
    int waitRetries = 10
    DeployPipeline(Script script) {
        super(script)
    }

    DeployPipeline(Script script, Map config) {
        super(script, config)
    }

    @Override
    void run() {
        withTestFailureHandling {
            deployPhase()
            nextPhase()
        }
    }

    def buildDockerImage() {
        script.node('master') {
            def dockerfilePath = "Dockerfile"
            def context = "."
            def version = "1.0.0"
            def originalImageName = "rest-api:${version}"
            def newImageName = "${GlobalVars.repository}/${GlobalVars.username}/${GlobalVars.group}/${originalImageName}"
            def buildResult = script.docker.build(
                    "${originalImageName}",
                    " -f ${dockerfilePath} ${context}"
            )
            println(buildResult.toString())
            tagAndPushImage(originalImageName, newImageName)
            removeImageFromLocal(newImageName)
        }
    }

    void tagAndPushImage(String originalImageName, String newImageName) {
        script.sh(script: "docker tag ${originalImageName} ${newImageName}")
        script.sh(script: "docker push ${newImageName}")
    }

    void removeImageFromLocal(String fullImageName) {
        def imageId = script.sh(
                script: "docker images -q ${fullImageName}",
                returnStdout: true
        ).trim()
        script.sh(script: "docker rmi --force ${imageId}")
    }

    boolean deployStatus(def job) {
        def time = new Date().getTime()
        def even = time % 2 == 0
        try {
            for (int i = 0; i < waitRetries; i++) {
                def (boolean status, Map results) = getStatus(even)
                String message = "- Execute ${job} => ${status} \n- Here => ${results}"
                if (status != null) {
                    if (!status) {
                        throw new PipelineException(message)
//                        script.error(message)
                    }
                    script.echo(message)
                    return status
                }
                script.sleep(10)
            }
        } catch(Exception exp) {
            script.echo(exp.getMessage())
            return false
//            throw new PipelineException(exp.getMessage())
        }
        script.catchError(stageResult: 'Failure') {
            script.error("Execute ${job} - timeout => Failed")
        }
        return false
    }

    def deployPhase() {
        Map<Integer, Closure> revertActions = [:]
        revertActions[0] = {
            script.stage("revert changes"){
                script.echo("Reverting ....")
            }
        }

        tryWithRevert({
            script.stage('Deploy') {
                script.node('master') {
                    def waitActions = [:]
                    def waitResults = [:]
                    int jobNumbers = 10
                    for (int index = 0; index < jobNumbers; index++) {
                        def actionName = "job-${index + 1}"
                        waitActions[actionName] = {
                            waitResults[actionName] = deployStatus(actionName)
                        }
                    }
                    script.parallel(waitActions)
                    boolean releasesOk = true
                    List<String> deploymentTargets = waitResults.keySet().toList()
                    for (int i = 0; i < deploymentTargets.size(); i++) {
                        String target = deploymentTargets[i]
                        Boolean result = waitResults[target]
                        if (result != Boolean.TRUE) {
                            script.echo("Release ${target} failed with result ${result}.")
                        }
                        releasesOk &= result
                    }

                    if (!releasesOk) {
                        throw new PipelineException("At least one deployment failed, aborting the pipeline")
                    }
                    script.echo("${deploymentTargets.size()} releases deployed successfully.")
                }
            }
        }, revertActions)
    }

    private def tryWithRevert(Closure action, Map<Integer, Closure> revertActions) {
        try {
            action.call()
        } catch (Exception e) {
            script.echo("Reverting actions - exception occurred: ${e}.")
            List<Integer> keys = revertActions.keySet().sort()
            script.echo("Executing revert actions: ${keys}")
            for (int i = 0; i < keys.size(); i++) {
                Integer action_order = keys[i]
                try {
                    script.echo("Executing revert action ${action_order}")
                    Closure revert_action = revertActions[action_order]
                    revert_action.call()
                    script.echo("Revert action ${action_order} done")
                } catch (Exception ex) {
                    script.echo("Revert action ${action_order} failed with exception ${ex} - check the log.")
                }
            }
            if (e instanceof TestFailureException) {
                // propagate test failure as such
                throw e
            }
            throw new PipelineException("Aborting pipeline - failed with exception ${e}", e)
        }
    }

    def displayInfo(List arr) {
        arr.each { item -> script.echo(item) }
    }

    def getStatus(even) {
        def value = Math.ceil(Math.random() * 10)
        if (even || value < 5) {
            return [null, [:]]
        }
        else if (value >= 5 && value <= 7) {
            return [false, ["Deploy Status": "Failed"]]
        }
        else if (value > 7) {
            return [true, ["Deploy Status": "Succeeded"]]
        }
        return [null, ["Deploy": "Timeout - Please double check again"]]
    }

    @NonCPS
    def nextPhase() {
        script.stage("Finish") {
            script.sayHello("You are welcome!")
        }
    }
}
