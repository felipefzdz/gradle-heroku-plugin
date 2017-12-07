package com.felipefzdz.gradle.heroku

import spock.lang.Requires

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

@Requires({
    GRADLE_HEROKU_PLUGIN_API_KEY && !GRADLE_HEROKU_PLUGIN_API_KEY.equals('null')
})
class CreateAppsFuncTest extends BaseFuncTest {

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
                apps {
                    app {
                        name = '$APP_NAME'
                        teamName = 'test'
                        stack = 'cedar-14'
                        personalApp = true
                    }
                }
            }
        """

        when:
        def result = run('herokuCreateApps')

        then:
        result.output.contains("Successfully created app $APP_NAME")
        result.task(":herokuCreateApps").outcome == SUCCESS

        and:
        herokuClient.appExists(APP_NAME)
    }
}
