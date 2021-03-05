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

    @NonCPS
    Void initPhase() {
        script.stage("Prepare") {
            script.echo("Hello, world")
            script.echo("The value of GlobalVars is : ${GlobalVars.name}")
            Map test = [:]
//            test['test1'] = {
//                test1: script.echo("1")
//                test2: script.echo("2")
//            }
//            test['test2'] = {
//                test3: script.echo("3")
//                test4: script.echo("4")
//            }
//            test["test1"] = {
//                arr1.forEach({ item -> script.echo("${item}") })
//            }


//            test["case1"] = {
//                script.steps("Test") {
//                    script.stage("test1") {
//                        script.echo("1")
//                        script.echo("2")
//                    }
//                    script.stage("test2") {
//                        script.echo("3")
//                        script.echo("4")
//                    }
//                }
//            }
                test = {
                    script.stage("test1") {
                        script.echo("1")
                        script.echo("2")
                    }
                    script.stage("test2") {
                        script.echo("3")
                        script.echo("4")
                    }
                }
                script.parallel(test)
        }
    }
    @NonCPS
    Void nextPhase() {
        script.stage("Next") {
            script.echo("You are welcome!")
        }
    }
}
