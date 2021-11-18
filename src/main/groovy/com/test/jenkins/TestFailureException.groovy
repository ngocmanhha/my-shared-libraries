package com.test.jenkins

class TestFailureException extends Exception {
    TestFailureException() {
        super()
    }

    TestFailureException(String s) {
        super(s)
    }

    TestFailureException(String s, Throwable throwable) {
        super(s, throwable)
    }

    TestFailureException(Throwable throwable) {
        super(throwable)
    }
}
