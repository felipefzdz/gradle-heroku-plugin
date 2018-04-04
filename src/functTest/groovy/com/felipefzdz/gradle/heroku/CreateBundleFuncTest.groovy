package com.felipefzdz.gradle.heroku

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class CreateBundleFuncTest extends BaseFuncTest {

    String APP_NAME = 'functional-test-app'
    String ANOTHER_APP_NAME = 'another-functional-test-app'

    @Override
    def getSubjectPlugin() {
        'heroku.base'
    }

    @Override
    def getMappingsDirectory() {
        'createBundle'
    }

    def cleanup() {
        herokuClient.destroyApp(APP_NAME)
        herokuClient.destroyApp(ANOTHER_APP_NAME)
    }

    def "can create a bundle"() {
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
        def result = run('herokuCreateBundle')

        then:
        result.output.contains("Successfully created app $APP_NAME")
        result.output.contains("Successfully created app $ANOTHER_APP_NAME")
        result.task(":herokuCreateBundle").outcome == SUCCESS

        and:
        herokuClient.appExists(APP_NAME)
        herokuClient.appExists(ANOTHER_APP_NAME)
    }
}
