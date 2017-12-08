package com.felipefzdz.gradle.heroku

import spock.lang.Requires

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

@Requires({
    GRADLE_HEROKU_PLUGIN_API_KEY && !GRADLE_HEROKU_PLUGIN_API_KEY.equals('null')
})
class DestroyAppFuncTest extends BaseFuncTest {

    String APP_NAME = 'functional-test-app'
    String ANOTHER_APP_NAME = 'another-functional-test-app'

    @Override
    def getSubjectPlugin() {
        'heroku-base'
    }

    def "can destroy an app"() {
        given:
        herokuClient.createApp(APP_NAME, 'test', true, 'cedar-14')

        buildFile << """
            heroku {
                apiKey = '$GRADLE_HEROKU_PLUGIN_API_KEY'
                bundle {
                    '$APP_NAME' {
                        teamName = 'test'
                        stack = 'cedar-14'
                        personalApp = true
                    }
                    '$ANOTHER_APP_NAME' {
                        teamName = 'test'
                        stack = 'heroku-16'
                        personalApp = true
                    }
                }
            }
        """

        when:
        def result = run("herokuDestroy${APP_NAME.capitalize()}")

        then:
        result.output.contains("Successfully destroyed app $APP_NAME")
        result.task(":herokuDestroy${APP_NAME.capitalize()}").outcome == SUCCESS

        and:
        !herokuClient.appExists(APP_NAME)
    }
}
