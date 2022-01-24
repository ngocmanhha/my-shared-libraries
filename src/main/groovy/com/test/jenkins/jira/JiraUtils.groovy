package com.test.jenkins.jira

import com.cloudbees.groovy.cps.NonCPS

//@Grapes([
//        @Grab(group = "net.rcarz", module = "jira-client", version = "0.5"),
//        @GrabConfig(systemClassLoader=true)
//])
@Grab(group = "net.rcarz", module = "jira-client", version = "0.5")
import net.rcarz.jiraclient.*;

class JiraUtils {
    Script script
    Map constants = [:]

    JiraUtils(Script script, Map constants) {
        this.script = script
        this.constants = constants
    }

    String doCreateIssue(String username, String password, String project, String issueType, String summary,
                         String description, String priority, List<String> labels) {
        /*def labelsString = '['+labels.collect { "\"$it\"" }.join( ',' ) +']'
        def jiraUrl = this.constants.jira.serverUrl as String
        BasicCredentials creds = new BasicCredentials("${username}", "${password}")
        JiraClient client = new JiraClient("${jiraUrl}", creds)
        def result = client.createIssue("${project}", "${issueType}")
                .field(Field.SUMMARY, "${summary}")
                .field(Field.DESCRIPTION, description)
                .field(Field.PRIORITY, "${priority}")
                .field(Field.LABELS, labelsString)
                .execute().toString();
        return result*/
        script.stage('Deploy') {
            script.node('master') {
                def labelsString = '['+labels.collect { "\"$it\"" }.join( ',' ) +']'
                def jiraUrl = constants.jira.serverUrl as String
                //Workaround: The current version of groovy doens't work well with creating issue
                def executeScript = """\
@Grab(group = "net.rcarz", module = "jira-client", version = "0.5")
import net.rcarz.jiraclient.*

BasicCredentials creds = new BasicCredentials("${username}", "${password}")
JiraClient client = new JiraClient("${jiraUrl}", creds)
def labels = ${labelsString}
def description = \"""${description}\"""
result = client.createIssue("${project}", "${issueType}")
        .field(Field.SUMMARY, "${summary}")
        .field(Field.DESCRIPTION, description)
        .field(Field.PRIORITY, "${priority}")
        .field(Field.LABELS, labels)
        .execute().toString();
println(result)
"""
                script.echo(executeScript)
                return script.sh(script: "/var/jenkins_home/groovy-2.4.12/bin/groovy -e '${executeScript}'", returnStdout: true).trim()
            }
        }
    }
}
