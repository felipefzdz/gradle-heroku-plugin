package com.felipefzdz.gradle.heroku

import spock.lang.Requires

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

@Requires({
    GRADLE_HEROKU_PLUGIN_API_KEY && !GRADLE_HEROKU_PLUGIN_API_KEY.equals('null')
})
class CreateAppFuncTest extends BaseFuncTest {

    @Override
    def getSubjectPlugin() {
        'heroku-base'
    }

    def cleanup() {
        run('herokuDestroyApp')
    }

    def "can create an app"() {
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
        def result = run('herokuCreateApp')

        then:
        result.output.contains("Successfully created app functional-test-app")
        result.task(":herokuCreateApp").outcome == SUCCESS

        and:
        herokuClient.appExists(appName)
    }
}
