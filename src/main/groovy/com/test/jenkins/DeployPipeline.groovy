package com.test.jenkins

import groovy.transform.InheritConstructors

@InheritConstructors
class DeployPipeline extends Pipeline {
    @Override
    void run() {
        initPhase()
    }

    def initPhase() {
        script.stage("Prepare") {
            script.sh("Hello, world")
            script.sh("The value of foo is : ${GlobalVars.name}")
            sayHello 'Job 0'
        }
    }
}
