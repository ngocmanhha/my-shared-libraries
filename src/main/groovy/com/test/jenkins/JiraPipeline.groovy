package com.test.jenkins

import com.cloudbees.groovy.cps.NonCPS
import com.test.jenkins.jira.JiraUtils
import groovy.transform.InheritConstructors

@InheritConstructors
class JiraPipeline extends Pipeline {
    JiraPipeline(Script script) {
        super(script)
    }

    JiraPipeline(Script script, Map config) {
        super(script, config)
    }

    @Override
    void run() {
        withTestFailureHandling {
            jiraPhase()
        }
    }

    def jiraPhase() {
        def constants = [
                "jira": [
                        "serverUrl": script.env.JIRA_SERVER_URL ?: "https://jira-stg.na.intgdc.com"
                ]
        ]
        try {
            JiraUtils jiraUtils = new JiraUtils(script, constants)
            String summary = "Update image gdc-c8s-base to version 3376a6a in gdc-docker-images"
            String description = """
            This ticket is automatically generated for tracking the update of the docker image gdc-c8s-base in repo gdc-docker-images via the automatically generated PR https://github.com/gooddata/gdc-docker-images/pull/528. Please merge this update as soon as possible.
    
            If a new update to the image happens while the linked PR isnt merged yet, that PR will be closed and a new one will be created automatically. This ticket will be closed and linked to a newly created ticket for tracking the new PR.
    
            If you encounter any problems or have other inquiries, please contact the SETI team.
            """
            def labels = ["ii-image-propagation","ii-image-propagation/gdc-docker-images/gdc-c8s-base"]
            String jiraUserName = script.env.JIRA_USER_NAME ?: 'admin'
            String jiraPassword = script.env.JIRA_PASSWORD ?: 'admin'
            def result = jiraUtils.doCreateIssue(jiraUserName, jiraPassword, "SETI", "Request", summary, description, "Blocker", labels)
            script.echo("${result}")
        } catch(Exception exp) {
            script.error("${exp.getMessage()}")
        }
    }
}
