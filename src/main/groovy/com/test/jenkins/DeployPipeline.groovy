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
            nextPhase()
        }
    }

//    @NonCPS
    def initPhase() {
        script.stage("Prepare") {
            script.echo("Hello, world")
            script.echo("The value of GlobalVars is : ${GlobalVars.name}")
            def test = [:]
            test['test1'] = {
                script.echo("1")
                script.echo("2")
            }
            test['test2'] = {
                script.echo("3")
                script.echo("4")
            }

//            test["case-1"] = {
//                script.stage("test1") {
//                    script.echo("1")
//                    script.echo("2")
//                }
//            }
//            test["case-2"] = {
//                script.stage("test2") {
//                    script.echo("3")
//                    script.echo("4")
//                }
//            }

//            ["1", "2", "3", "4"].each {item ->
//                test["case-${item}"] = {
//                    displayInfo(["${item}"])
//                }
//            }
            script.parallel(test)
        }
    }

//    @NonCPS
    def displayInfo(List arr) {
        arr.each { item -> script.echo(item) }
    }

    @NonCPS
    def nextPhase() {
        script.stage("Next") {
            script.echo("You are welcome!")
        }
    }
}
