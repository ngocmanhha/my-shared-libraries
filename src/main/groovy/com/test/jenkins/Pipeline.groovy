package com.test.jenkins

abstract class Pipeline implements Serializable {
    Script script
    Pipeline(Script script) {
        this.script = script
    }
    static Pipeline resolve(Script script) {
        construct(script)
    }

    private static Pipeline construct(Script script, Map config, Map constants) {
        // resolve pipeline type
        script.echo("pipeline type: Test")
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
