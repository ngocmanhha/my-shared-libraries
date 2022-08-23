package com.test.jenkins

import groovy.transform.InheritConstructors

@InheritConstructors
class TestPipeline extends Pipeline {

    TestPipeline(Script script) {
        super(script)
    }

    TestPipeline(Script script, Map config) {
        super(script, config)
    }

    @Override
    void run() {
        withTestFailureHandling {
            buildPhase()
        }
    }

    def buildPhase() {
        script.stage("Build") {
            def test = [:]
            ["1", "2", "3", "4"].each {item ->
                test["build-${item}"] = {
                    if (item == "3") {
                        script.node('master') {
                            script.sh "exit 1";
                        }
                    }
                }
            }
            script.parallel(test)
        }
    }
}
