package com.felipefzdz.gradle.heroku

import spock.lang.Requires

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

@Requires({
    GRADLE_HEROKU_PLUGIN_API_KEY && !GRADLE_HEROKU_PLUGIN_API_KEY.equals('null')
})
class InstallAddonsFuncTest extends BaseFuncTest {

    String APP_NAME = 'functional-test-app'

    @Override
    def getSubjectPlugin() {
        'heroku-base'
    }

    def cleanup() {
        herokuClient.destroyApp(APP_NAME)
    }

    def "can install addons for an app"() {
        given:
        herokuClient.createApp(APP_NAME, 'test', true)

        buildFile << """
            heroku {
                apiKey = '$GRADLE_HEROKU_PLUGIN_API_KEY'
                appName = '$APP_NAME'
                addons {
                    redis {
                        plan = 'heroku-redis:hobby-dev'
                    } 
                }
            }
        """

        when:
        def result = run('herokuInstallAddons')

        then:
        result.output.contains("Successfully installed addon REDIS")
        result.task(":herokuInstallAddons").outcome == SUCCESS

        and:
        herokuClient.getAddonAttachments(APP_NAME)*.name == ['REDIS']
    }
}
