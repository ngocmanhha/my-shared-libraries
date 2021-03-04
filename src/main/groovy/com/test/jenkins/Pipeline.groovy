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
        script.echo("pipeline type: Test")
        return new DeployPipeline(script);
    }

    protected void withTestFailureHandling(Closure action) {
        try {
            action.call()
        } catch (Exception e) {
            // abort the pipeline without throwing an exception
            return
        }
    }

    abstract void run();
}
