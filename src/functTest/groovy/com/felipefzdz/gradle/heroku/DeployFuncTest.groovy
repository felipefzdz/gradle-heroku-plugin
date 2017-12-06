package com.felipefzdz.gradle.heroku

import spock.lang.Requires

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

@Requires({
    GRADLE_HEROKU_PLUGIN_API_KEY && !GRADLE_HEROKU_PLUGIN_API_KEY.equals('null')
})
class DeployFuncTest extends BaseFuncTest {

    @Override
    def getSubjectPlugin() {
        'heroku'
    }

    def cleanup() {
        run('herokuDestroyApp')
    }

    def "can deploy an app"() {
        given:
        def appName = 'functional-test-app'
        buildFile << """
            heroku {
                apiKey = '$GRADLE_HEROKU_PLUGIN_API_KEY'
                appName = '$appName'
                teamName = 'test'
                personalApp = true
            }
        """

        when:
        def result = run('herokuDeploy')

        then:
        result.output.contains("Successfully deployed app functional-test-app")
        result.task(":herokuDeploy").outcome == SUCCESS

        and:
        herokuClient.appExists(appName)
    }

}
