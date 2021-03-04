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
    def initPhase() {
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
            List<String> arr1 = new ArrayList<>();
            arr1.add("1");
            arr1.add("2");
            List<String> arr2 = new ArrayList<>();
            arr1.add("3");
            arr1.add("4");
//            test["test1"] = {
//                arr1.forEach({ item -> script.echo("${item}") })
//            }
            test["case1"] = {
                script.stage("test1") {
                    script.echo("1")
                    script.echo("2")
                }
            }
            test["case2"] = {
                script.stage("test2") {
                    script.echo("3")
                    script.echo("4")
                }
            }
            script.parallel(test)
        }
    }
    @NonCPS
    def nextPhase() {
        script.stage("Next") {
            script.echo("You are welcome!")
        }
    }
}
