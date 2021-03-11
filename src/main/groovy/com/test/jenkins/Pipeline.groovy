package com.test.jenkins

abstract class Pipeline implements Serializable {
    Script script
    Map config = [
        constants: [
            pipeline: [
                build: [
                        timeout: [:]
//                    timeout: [
//                        time: 10,
//                        unit: 'MINUTES'
////                        unit: 'MILLISECONDS'
//                    ]
                ]
            ]
        ]
    ]

    Pipeline(Script script, Map config) {
        this.script = script
        this.config = config;
    }

    Pipeline(Script script) {
        this.script = script
    }

    static Pipeline resolve(Script script, Map config) {
        construct(script, config)
    }

    static Pipeline resolve(Script script) {
        construct(script)
    }

    private static Pipeline construct(Script script, Map config) {
        // resolve pipeline type
        script.echo("Pipeline type")
        return new DeployPipeline(script, config);
    }

    private static Pipeline construct(Script script) {
        // resolve pipeline type
        script.echo("Pipeline type")
        return new DeployPipeline(script);
    }

    private def startPipeline(Map timeout = [:], Closure act) {
        if (!timeout?.time) {
//            script.println("Missing time config")
//            return
            act.call()
        }
        if (timeout?.unit) {
            if (validateUnit(timeout.unit)) {
                script.timeout(
                        time: timeout.time,
                        unit: timeout.unit
                ) {
                    act.call()
                }
            }
            else {
                script.println("Timeout unit is not match any in ${TimeoutUnit.values().toString()}");
                return
            }
        }
        else {
            script.println("Missing unit config")
            return
        }
    }

    private def validateUnit(String timeoutUnit) {
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
            startPipeline(config.constants.pipeline.build.timeout, action)
        } catch (Exception e) {
            // abort the pipeline without throwing an exception
            script.print(e.getMessage());
            throw e;
        }
    }

    abstract void run();
}
