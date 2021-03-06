package com.felipefzdz.gradle.heroku

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class DestroyBundleFuncTest extends BaseFuncTest {

    String APP_NAME = 'functional-test-app'
    String ANOTHER_APP_NAME = 'another-functional-test-app'

    @Override
    def getSubjectPlugin() {
        'heroku.base'
    }

    @Override
    def getMappingsDirectory() {
        'destroyBundle'
    }

    def "can destroy a bundle"() {
        given:
        herokuClient.createApp(APP_NAME, 'test', true, 'cedar-14')
        herokuClient.createApp(ANOTHER_APP_NAME, 'test', true, 'cedar-14')

        buildFile << """
            import com.felipefzdz.gradle.heroku.tasks.model.HerokuWebApp

            heroku {
                bundles {
                    dev {
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
            }
        """

        when:
        def result = run('herokuDestroyDevBundle')

        then:
        result.output.contains("Successfully destroyed app $APP_NAME")
        result.output.contains("Successfully destroyed app $ANOTHER_APP_NAME")
        result.task(":herokuDestroyDevBundle").outcome == SUCCESS

        and:
        !herokuClient.appExists(APP_NAME)
        !herokuClient.appExists(ANOTHER_APP_NAME)
    }
}
