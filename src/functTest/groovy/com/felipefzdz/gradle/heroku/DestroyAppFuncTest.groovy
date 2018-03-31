package com.felipefzdz.gradle.heroku

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class DestroyAppFuncTest extends BaseFuncTest {

    String APP_NAME = 'functional-test-app'
    String ANOTHER_APP_NAME = 'another-functional-test-app'

    @Override
    def getSubjectPlugin() {
        'heroku-base'
    }

    @Override
    def getMappingsDirectory() {
        'destroyApp'
    }

    def "can destroy an app"() {
        given:
        herokuClient.createApp(APP_NAME, 'test', true, 'cedar-14')

        buildFile << """
            import com.felipefzdz.gradle.heroku.tasks.model.HerokuWebApp

            heroku {
                apiKey = '$GRADLE_HEROKU_PLUGIN_API_KEY'
                bundle {
                    '$APP_NAME'(HerokuWebApp) {
                        teamName = 'test'
                        stack = 'cedar-14'
                        personalApp = true
                    }
                    '$ANOTHER_APP_NAME'(HerokuWebApp) {
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
