#!/usr/bin/env groovy
package com.test.jenkins.vars

def call(String message = 'Hello, welcome!') {
    echo "${message}"
}