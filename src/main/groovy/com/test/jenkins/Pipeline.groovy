package com.test.jenkins

abstract class Pipeline implements Serializable {
    Script script
    Map config
    Pipeline(Script script, Map config) {
        this.script = script
        this.config = config;
    }
    static Pipeline resolve(Script script, Map config) {
        construct(script, config)
    }

    private static Pipeline construct(Script script, Map config) {
        // resolve pipeline type
        script.echo("Pipeline type")
        return new DeployPipeline(script, config);
    }

    private void setTimeout(List timeout, Closure action) {
        if (!timeout?.time) {
            script.println("Missing time config")
            return
        }
        if (timeout?.unit) {
            if (validateUnit(timeout.unit)) {
                script.timeout(
                        time: timeout.time,
                        unit: timeout.unit
                ) {
                    action.call()
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
            setTimeout(config.constants.pipeline.build.timeout, action)
        } catch (Exception e) {
            // abort the pipeline without throwing an exception
            script.print(e.getMessage());
            throw e;
        }
    }

    abstract void run();
}
