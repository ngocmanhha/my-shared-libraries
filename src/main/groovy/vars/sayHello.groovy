#!/usr/bin/env groovy
package com.test.jenkins.vars

def call(String name = 'Lucas') {
    echo "Hello, ${name}"
}