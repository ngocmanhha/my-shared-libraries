package com.test.jenkins

import groovy.transform.InheritConstructors

@InheritConstructors
class BuildPipeline extends Pipeline {

    BuildPipeline(Script script) {
        super(script)
    }

    BuildPipeline(Script script, Map config) {
        super(script, config)
    }

    @Override
    void run() {
        withTestFailureHandling {
            buildPhase()
        }
    }

    def buildPhase() {
        script.stage("Build") {
            def test = [:]
            ["1", "2", "3", "4"].each {item ->
                test["build-${item}"] = {
                    displayInfo(["${item}"])
                }
            }
            test["build-image"] = {
                buildDockerImage()
            }
            script.parallel(test)
        }
    }

    def buildDockerImage() {
        script.node('master') {
            def dockerfilePath = "Dockerfile"
            def context = "."
            def version = "1.0.0"
            def originalImageName = "rest-api:${version}"
            def newImageName = "${GlobalVars.repository}/${GlobalVars.username}/${GlobalVars.group}/${originalImageName}"
            def buildResult = script.docker.build(
                    "${originalImageName}",
                    " -f ${dockerfilePath} ${context}"
            )
            println(buildResult.toString())
            tagAndPushImage(originalImageName, newImageName)
            removeImageFromLocal(newImageName)
        }
    }

    void tagAndPushImage(String originalImageName, String newImageName) {
        script.sh(script: "docker tag ${originalImageName} ${newImageName}")
        script.sh(script: "docker push ${newImageName}")
    }

    void removeImageFromLocal(String fullImageName) {
        def imageId = script.sh(
                script: "docker images -q ${fullImageName}",
                returnStdout: true
        ).trim()
        script.sh(script: "docker rmi --force ${imageId}")
    }

}
