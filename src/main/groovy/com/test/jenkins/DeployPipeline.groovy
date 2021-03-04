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
            script.parallel({
                test: {
                    test1: script.echo("1")
                    test2: script.echo("2")
    //                test2: script.node() {
    //                    script.sh("echo 2")
    //                }
                }
                test2: {
                    test3: script.echo("3")
                    test4: script.echo("4")
                }
            })
//            script.parallel(
////                test2: script.node() {
////                    script.sh("echo 2")
////                }
//            })
        }
    }
    @NonCPS
    def nextPhase() {
        script.stage("Next") {
            script.echo("The value of GlobalVars is : ${GlobalVars.name}")
//            sayHello 'Job 1'
        }
    }
}
