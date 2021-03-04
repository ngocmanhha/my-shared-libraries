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
            sayHello 'Job 0'
        }
    }
    @NonCPS
    def nextPhase() {
        script.stage("Next") {
            script.echo("The value of GlobalVars is : ${GlobalVars.name}")
            sayHello 'Job 1'
        }
    }
}
