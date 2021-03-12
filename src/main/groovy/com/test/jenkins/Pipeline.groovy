package com.test.jenkins

import javax.security.auth.login.Configuration

abstract class Pipeline implements Serializable {
    Script script
    Map config = [
        constants: [
            pipeline: [
                build: [
                        timeout: [:]
//                    timeout: [
//                        time: 10,
////                        unit: 'MINUTES'
////                        unit: 'MILLISECONDS'
//                    ]
                ]
            ]
        ]
    ]

    Pipeline(Script script, Map config) {
        this.script = script
        this.config = config
    }

    Pipeline(Script script) {
        this.script = script
    }

    static Pipeline resolve(Script script) {
        construct(script, [:])
    }

//    static Pipeline resolve(Script script) {
//        construct(script)
//    }

    private static Pipeline construct(Script script, Map config) {
        // resolve pipeline type
        script.echo("Pipeline type")
        Map configuration = [:]

        configuration.pipelineDefinition = config
        configuration.build = prepareBuildVariables(script, configuration)
        Map configs = [
            constants: prepareBuildVariables(script, configuration)
        ]
        Map timeout = configs.constants.pipeline.build.timeout
        if (!timeout?.time) {
            return new DeployPipeline(script, config)
        }
        if (timeout?.unit) {
            if (validateUnit(timeout.unit)) {
                script.timeout(
                        time: timeout.time,
                        unit: timeout.unit
                ) {
                    return new DeployPipeline(script, config)
                }
            }
            else {
                throw new Exception("Timeout unit is not match any in ${TimeoutUnit.values().toString()}")
                return
            }
        }
        else {
            throw new Exception("Missing unit config")
            return
        }
//        return new DeployPipeline(script, configs)
    }

    private static Map prepareBuildVariables(Script script, Map configuration) {
        script.node('master') {
            script.stage('prepare-build-variables') {
                Map scmVars = script.checkout(script.scm)
                def configFile = ".jenkins-ci.yaml"
                Map configs = script.readYaml(text: script.readFile(file: configFile))
                script.echo("Loaded config yaml: ${configs}")
                return configs
            }
        }
    }

    private static Pipeline construct(Script script) {
        // resolve pipeline type
        script.echo("Pipeline type")
        return new DeployPipeline(script)
    }

    protected Pipeline startPipeline(Script script, Map config, Map timeout, Pipeline pipeline) {
//        if (!timeout?.time) {
//            return new DeployPipeline(script, config)
//        }
//        if (timeout?.unit) {
//            if (validateUnit(timeout.unit)) {
//                script.timeout(
//                        time: timeout.time,
//                        unit: timeout.unit
//                ) {
//                    return new DeployPipeline(script, config)
//                }
//            }
//            else {
//                throw new Exception("Timeout unit is not match any in ${TimeoutUnit.values().toString()}")
//                return
//            }
//        }
//        else {
//            throw new Exception("Missing unit config")
//            return
//        }
    }

    protected def validateUnit(String timeoutUnit) {
        try {
            return TimeoutUnit.valueOf(timeoutUnit)
        }
        catch (Exception e) {
            script.println(e.getMessage())
            return false
        }
    }

    protected void withTestFailureHandling(Closure action) {
        try {
            action.run()
        } catch (Exception e) {
            // abort the pipeline without throwing an exception
            script.print(e.getMessage());
            throw e;
        }
    }

    abstract void run();
}
