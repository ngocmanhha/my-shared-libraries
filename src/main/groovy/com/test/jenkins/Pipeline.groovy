package com.test.jenkins

abstract class Pipeline implements Serializable {
    Script script
    Pipeline(Script script) {
        this.script = script
    }
    Pipeline resolve(Script script) {
        this.script = script
    }
    abstract void run();
}
