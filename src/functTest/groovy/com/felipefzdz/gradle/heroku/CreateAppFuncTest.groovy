package com.felipefzdz.gradle.heroku

import spock.lang.Requires

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

@Requires({
    GRADLE_HEROKU_PLUGIN_API_KEY && !GRADLE_HEROKU_PLUGIN_API_KEY.equals('null')
})
class CreateAppFuncTest extends BaseFuncTest {

    String APP_NAME = 'functional-test-app'

    @Override
    def getSubjectPlugin() {
        'heroku-base'
    }

    def cleanup() {
        herokuClient.destroyApp(APP_NAME)
    }

    def "can create an app"() {
        given:
        buildFile << """
            heroku {
                apiKey = '$GRADLE_HEROKU_PLUGIN_API_KEY'
                appName = '$APP_NAME'
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
        herokuClient.appExists(APP_NAME)
    }
}
