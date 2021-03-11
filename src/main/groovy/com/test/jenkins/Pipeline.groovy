package com.test.jenkins

abstract class Pipeline implements Serializable {
    Script script

    Pipeline(Script script) {
        this.script = script
    }
    static Pipeline resolve(Script script) {
        construct(script)
    }

    private static Pipeline construct(Script script) {
        // resolve pipeline type
        script.echo("Pipeline type")
        return new DeployPipeline(script);
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
            setTimeout(config.constants.pipeline.build.timeout as List, action)
        } catch (Exception e) {
            // abort the pipeline without throwing an exception
            script.print(e.getMessage());
            throw e;
        }
    }

    abstract void run();
}
