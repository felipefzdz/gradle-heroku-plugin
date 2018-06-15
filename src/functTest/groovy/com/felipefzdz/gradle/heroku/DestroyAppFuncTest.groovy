package com.felipefzdz.gradle.heroku

import static com.felipefzdz.gradle.heroku.utils.FormatUtil.toUpperCamel
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class DestroyAppFuncTest extends BaseFuncTest {

    String APP_NAME = 'functional-test-app'
    String ANOTHER_APP_NAME = 'another-functional-test-app'

    @Override
    def getSubjectPlugin() {
        'heroku.base'
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
        def result = run("herokuDestroyDev${toUpperCamel(APP_NAME)}")

        then:
        result.output.contains("Successfully destroyed app $APP_NAME")
        result.task(":herokuDestroyDev${toUpperCamel(APP_NAME)}").outcome == SUCCESS

        and:
        !herokuClient.appExists(APP_NAME)
    }
}
