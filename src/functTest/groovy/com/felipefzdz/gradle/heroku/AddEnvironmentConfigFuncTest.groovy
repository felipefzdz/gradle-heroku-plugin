package com.felipefzdz.gradle.heroku

import static com.felipefzdz.gradle.heroku.utils.FormatUtil.toUpperCamel
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class AddEnvironmentConfigFuncTest extends BaseFuncTest {

    String APP_NAME = 'functional-test-app'

    @Override
    def getSubjectPlugin() {
        'heroku.base'
    }

    @Override
    def getMappingsDirectory() {
        'addEnvironmentConfig'
    }

    def cleanup() {
        herokuClient.destroyApp(APP_NAME)
    }

    def "can add environment config for an app"() {
        given:
        herokuClient.createApp(APP_NAME, 'test', true, 'cedar-14')
        buildFile << """
            import com.felipefzdz.gradle.heroku.tasks.model.HerokuWebApp

            heroku {
                bundles {
                    dev {
                        '$APP_NAME'(HerokuWebApp) {
                            config {
                                configToBeExpected = ['MODE': 'dev', 'API_KEY': 'secret']
                            }
                        }
                    }
                }
            }
        """

        when:
        def result = run("herokuAddEnvironmentConfigForDev${toUpperCamel(APP_NAME)}")

        then:
        result.output.contains("Added environment config for $APP_NAME")
        result.task(":herokuAddEnvironmentConfigForDev${toUpperCamel(APP_NAME)}").outcome == SUCCESS

        and:
        def config = herokuClient.listConfig(APP_NAME)
        config.keySet().containsAll(['MODE', 'API_KEY'])
        config.values().containsAll(['dev', 'secret'])
    }

}
