package com.felipefzdz.gradle.heroku

import spock.lang.Requires

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

@Requires({
    GRADLE_HEROKU_PLUGIN_API_KEY && !GRADLE_HEROKU_PLUGIN_API_KEY.equals('null')
})
class DestroyAppFuncTest extends BaseFuncTest {

    String APP_NAME = 'functional-test-app'

    @Override
    def getSubjectPlugin() {
        'heroku-base'
    }

    def "can destroy an app"() {
        given:
        herokuClient.createApp(APP_NAME, 'test', true)

        buildFile << """
            heroku {
                apiKey = '$GRADLE_HEROKU_PLUGIN_API_KEY'
                appName = '$APP_NAME'
            }
        """

        when:
        def result = run('herokuDestroyApp')

        then:
        result.output.contains("Successfully destroyed app functional-test-app")
        result.task(":herokuDestroyApp").outcome == SUCCESS

        and:
        !herokuClient.appExists(APP_NAME)
    }
}
