package com.test.jenkins

import groovy.transform.InheritConstructors
import com.cloudbees.groovy.cps.NonCPS

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
            script.docker.build(
                    "rest-api:1.0.0",
                    " -f ${dockerfilePath} ${context}"
            )
        }
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
