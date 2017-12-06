package com.felipefzdz.gradle.heroku

import spock.lang.Requires

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

@Requires({
    GRADLE_HEROKU_PLUGIN_API_KEY && !GRADLE_HEROKU_PLUGIN_API_KEY.equals('null')
})
class DestroyAppFuncTest extends BaseFuncTest {

    @Override
    def getSubjectPlugin() {
        'heroku-base'
    }

    def "can destroy an app"() {
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
        run('herokuCreateApp')

        when:
        def result = run('herokuDestroyApp')

        then:
        result.output.contains("Successfully destroyed app functional-test-app")
        result.task(":herokuDestroyApp").outcome == SUCCESS

        and:
        !herokuClient.appExists(appName)
    }
}
