package com.test.jenkins

class PipelineException extends Exception {
    PipelineException() {
        super()
    }

    PipelineException(String s) {
        super(s)
    }

    PipelineException(String s, Throwable throwable) {
        super(s, throwable)
    }

    PipelineException(Throwable throwable) {
        super(throwable)
    }
}
