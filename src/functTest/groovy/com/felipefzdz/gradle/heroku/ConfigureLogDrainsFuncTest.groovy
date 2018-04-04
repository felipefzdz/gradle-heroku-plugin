package com.felipefzdz.gradle.heroku

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class ConfigureLogDrainsFuncTest extends BaseFuncTest {

    String APP_NAME = 'functional-test-app'
    String LOG_DRAIN_URL = 'syslog://logs.example.com'
    String ANOTHER_LOG_DRAIN_URL = 'syslog://another-logs.example.com'

    @Override
    def getSubjectPlugin() {
        'heroku-base'
    }

    @Override
    def getMappingsDirectory() {
        'configureLogDrains'
    }

    def cleanup() {
        herokuClient.destroyApp(APP_NAME)
    }

    def "can configure log drains for an app"() {
        given:
        herokuClient.createApp(APP_NAME, 'test', true, 'cedar-14')
        buildFile << """
            import com.felipefzdz.gradle.heroku.tasks.model.HerokuWebApp

            heroku {
                bundle {
                    '$APP_NAME'(HerokuWebApp) {
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
