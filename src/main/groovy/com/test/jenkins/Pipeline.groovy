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

    protected void withTestFailureHandling(Closure action) {
        try {
            script.retry(count: 3) {
                script.timeout(
                        time: config.constants.pipeline.build.timeout.time,
                        unit: config.constants.pipeline.build.timeout.unit
                ) {
                    action.call()
                }
            }
        } catch (Exception e) {
            // abort the pipeline without throwing an exception
            script.print(e.getMessage());
            throw e;
        }
    }

    abstract void run();
}
