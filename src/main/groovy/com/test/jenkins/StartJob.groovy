#!/usr/bin/env groovy

package com.test.jenkins;

class StartJob {
    String jobname;
    def setJobName(String jobname) {
        this.jobname = jobname;
    }
}