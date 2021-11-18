package com.test.jenkins

import groovy.transform.InheritConstructors

@InheritConstructors
class CheckPipeline extends Pipeline {

    CheckPipeline(Script script) {
        super(script)
    }

    CheckPipeline(Script script, Map config) {
        super(script, config)
    }

    @Override
    void run() {
        withTestFailureHandling {
            initPhase()
            testPhase()
        }
    }

    def initPhase() {
        script.stage("Prepare") {
//            script.retry(count: 3) {
//                script.echo(a)
//            }
            script.echo("Hello, world")
            script.echo("The value of GlobalVars is : ${GlobalVars.name}")
//            script.node('master') {
//                Map scmVars = script.checkout(script.scm)
//            }
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
}
