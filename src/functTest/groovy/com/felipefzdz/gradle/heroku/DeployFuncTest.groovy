package com.felipefzdz.gradle.heroku

import spock.lang.Requires

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

@Requires({
    GRADLE_HEROKU_PLUGIN_API_KEY && !GRADLE_HEROKU_PLUGIN_API_KEY.equals('null')
})
class DeployFuncTest extends BaseFuncTest {

    String APP_NAME = 'functional-test-app'

    @Override
    def getSubjectPlugin() {
        'heroku'
    }

    def cleanup() {
        herokuClient.destroyApp(APP_NAME)
    }

    def "can deploy an app"() {
        given:
        buildFile << """
            heroku {
                apiKey = '$GRADLE_HEROKU_PLUGIN_API_KEY'
                appName = '$APP_NAME'
                teamName = 'test'
                stack = 'heroku-16'
                personalApp = true
                addons {
                    redis {
                        plan = 'heroku-redis:hobby-dev'
                    } 
                }
            }
        """

        when:
        def result = run('herokuDeploy')

        then:
        result.output.contains("Successfully deployed app functional-test-app")
        result.task(":herokuDeploy").outcome == SUCCESS

        and:
        herokuClient.appExists(APP_NAME)

        and:
        herokuClient.getAddonAttachments(APP_NAME)*.name == ['REDIS']
    }

}
