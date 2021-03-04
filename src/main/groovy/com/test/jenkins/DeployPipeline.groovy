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
        }
    }

    @NonCPS
    def initPhase() {
        script.stage("Prepare") {
            script.echo("Hello, world")
            script.echo("The value of foo is : ${GlobalVars.name}")
            sayHello 'Job 0'
        }
    }
}
