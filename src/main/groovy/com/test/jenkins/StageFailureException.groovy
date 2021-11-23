package com.test.jenkins

class StageFailureException extends Exception {
    StageFailureException() {
        super()
    }

    StageFailureException(String s) {
        super(s)
    }

    StageFailureException(String s, Throwable throwable) {
        super(s, throwable)
    }

    StageFailureException(Throwable throwable) {
        super(throwable)
    }
}
