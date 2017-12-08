package com.felipefzdz.gradle.heroku

import spock.lang.Requires

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

@Requires({
    GRADLE_HEROKU_PLUGIN_API_KEY && !GRADLE_HEROKU_PLUGIN_API_KEY.equals('null')
})
class ConfigureLogDrainsForAppFuncTest extends BaseFuncTest {

    String APP_NAME = 'functional-test-app'
    String LOG_DRAIN_URL = 'syslog://logs.example.com'
    String ANOTHER_LOG_DRAIN_URL = 'syslog://another-logs.example.com'

    @Override
    def getSubjectPlugin() {
        'heroku'
    }

    def cleanup() {
        herokuClient.destroyApp(APP_NAME)
    }

    def "can configure log drains for an app"() {
        given:
        herokuClient.createApp(APP_NAME, 'test', true, 'cedar-14')
        buildFile << """
            heroku {
                apiKey = '$GRADLE_HEROKU_PLUGIN_API_KEY'
                bundle {
                    '$APP_NAME' {
                        logDrains = ['$LOG_DRAIN_URL', '$ANOTHER_LOG_DRAIN_URL']
                    }
                }
            }
        """

        when:
        def result = run("herokuConfigureLogDrainsFor${APP_NAME.capitalize()}")

        then:
        result.output.contains("Added log drain $LOG_DRAIN_URL")
        result.output.contains("Added log drain $ANOTHER_LOG_DRAIN_URL")
        result.task(":herokuConfigureLogDrainsFor${APP_NAME.capitalize()}").outcome == SUCCESS

        and:
        herokuClient.listLogDrains(APP_NAME)*.url.containsAll([LOG_DRAIN_URL, ANOTHER_LOG_DRAIN_URL])
    }

}
