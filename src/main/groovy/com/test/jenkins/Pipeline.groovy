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

    static Pipeline resolve(Script script, Map config) {
        def configFile = ".jenkins-ci.yaml"
        Map configs = script.readYaml(text: script.readFile(file: configFile))
        script.echo("Loaded config yaml: ${configs}")
        config.constants.pipeline = configs
        script.echo(config)
        construct(script, config)
    }

    static Pipeline resolve(Script script) {
        construct(script)
    }

    private static Pipeline construct(Script script, Map config) {
        // resolve pipeline type
        script.echo("Pipeline type")
//        return new DeployPipeline(script, config)
        return startPipeline(DeployPipeline.class)
    }

    private static Pipeline construct(Script script) {
        // resolve pipeline type
        script.echo("Pipeline type")
//        return new DeployPipeline(script, config)
        return startPipeline(DeployPipeline.class)
    }

    protected Pipeline startPipeline(Class<?> cls) {
        cls = new Class<>(script);
        def timeout = script.constants.pipeline.build.timeout
        script.echo("${timeout.toString()}")

        if (!timeout?.time) {
//            pipeline.run()
            return (Pipeline)cls
        }
        if (timeout?.unit) {
            if (validateUnit(timeout.unit)) {
                script.timeout(
                        time: timeout.time,
                        unit: timeout.unit
                ) {
//                    pipeline.run()
                    return (Pipeline)cls
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
