package com.felipefzdz.gradle.heroku

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class AddEnvironmentConfigFuncTest extends BaseFuncTest {

    String APP_NAME = 'functional-test-app'

    @Override
    def getSubjectPlugin() {
        'heroku-base'
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
                apiKey = '$GRADLE_HEROKU_PLUGIN_API_KEY'
                bundle {
                    '$APP_NAME'(HerokuWebApp) {
                        config = ['MODE': 'dev', 'API_KEY': 'secret']
                    }
                }
            }
        """

        when:
        def result = run("herokuAddEnvironmentConfigFor${APP_NAME.capitalize()}")

        then:
        result.output.contains("Added environment config for $APP_NAME")
        result.task(":herokuAddEnvironmentConfigFor${APP_NAME.capitalize()}").outcome == SUCCESS

        and:
        def config = herokuClient.listConfig(APP_NAME)
        config.keySet().containsAll(['MODE', 'API_KEY'])
        config.values().containsAll(['dev', 'secret'])
    }

}
