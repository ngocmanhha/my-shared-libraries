package com.test.jenkins

import groovy.transform.InheritConstructors
import com.cloudbees.groovy.cps.NonCPS
import com.test.jenkins.GlobalVars

@InheritConstructors
class DeployPipeline extends Pipeline {
    DeployPipeline(Script script) {
        super(script)
    }

    @Override
    void run() {
        withTestFailureHandling {
            initPhase()
            testPhase()
            buildPhase()
            deployPhase()
            nextPhase()
        }
    }

    def initPhase() {
        script.stage("Prepare") {
            script.echo("Hello, world")
            script.echo("The value of GlobalVars is : ${GlobalVars.name}")
            script.node('master') {
                Map scmVars = script.checkout(script.scm)
            }
        }
    }

    def testPhase() {
        script.stage("Test") {
            def test = [:]
            test["test-1"] = {
                script.stage("test1") {
                    script.echo("1")
                    script.echo("2")
                }
            }
            test["test-2"] = {
                script.stage("test2") {
                    script.echo("3")
                    script.echo("4")
                }
            }

            script.parallel(test)
        }
    }

    def buildPhase() {
        script.stage("Build") {
            def test = [:]
            ["1", "2", "3", "4"].each {item ->
                test["build-${item}"] = {
                    displayInfo(["${item}"])
                }
            }
            test["build-image"] = {
                buildDockerImage()
            }
            script.parallel(test)
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
                script: "docker images -q ${image.fullImageName()}",
                returnStdout: true).trim()
        script.sh(script: "docker rmi --force ${imageId}")
    }

    def deployPhase() {
        script.stage('Deploy') {
            def test = [:]
            test['deploy-1'] = {
                script.echo("1")
                script.echo("2")
            }
            test['deploy-2'] = {
                script.echo("3")
                script.echo("4")
            }
            script.parallel(test)
        }
    }

    def displayInfo(List arr) {
        arr.each { item -> script.echo(item) }
    }

    @NonCPS
    def nextPhase() {
        script.stage("Finish") {
            script.echo("You are welcome!")
        }
    }
}
