package com.felipefzdz.gradle.heroku

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class CreateAppFuncTest extends BaseFuncTest {

    String APP_NAME = 'functional-test-app'
    String ANOTHER_APP_NAME = 'another-functional-test-app'

    @Override
    def getSubjectPlugin() {
        'heroku.base'
    }

    @Override
    def getMappingsDirectory() {
        'createApp'
    }

    def "can create an app"() {
        given:
        buildFile << """
            import com.felipefzdz.gradle.heroku.tasks.model.HerokuWebApp

            heroku {
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
        def result = run("herokuCreate${APP_NAME.capitalize()}")

        then:
        result.output.contains("Successfully created app $APP_NAME")
        result.task(":herokuCreate${APP_NAME.capitalize()}").outcome == SUCCESS

        and:
        herokuClient.appExists(APP_NAME)
    }
}
