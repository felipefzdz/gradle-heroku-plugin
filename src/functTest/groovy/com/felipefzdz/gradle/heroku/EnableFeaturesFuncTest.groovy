package com.felipefzdz.gradle.heroku

import spock.lang.Requires

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

@Requires({
    GRADLE_HEROKU_PLUGIN_API_KEY && !GRADLE_HEROKU_PLUGIN_API_KEY.equals('null')
})
class EnableFeaturesFuncTest extends BaseFuncTest {

    String APP_NAME = 'functional-test-app'
    String FEATURE = 'http-session-affinity'
    String ANOTHER_FEATURE = 'spaces-dns-discovery'

    @Override
    def getSubjectPlugin() {
        'heroku-base'
    }

    def cleanup() {
        herokuClient.destroyApp(APP_NAME)
    }

    def "can enable features for an app"() {
        given:
        herokuClient.createApp(APP_NAME, 'test', true, 'cedar-14')
        buildFile << """
            import com.felipefzdz.gradle.heroku.tasks.model.HerokuWebApp

            heroku {
                apiKey = '$GRADLE_HEROKU_PLUGIN_API_KEY'
                bundle {
                    '$APP_NAME'(HerokuWebApp) {
                        features = ['$FEATURE', '$ANOTHER_FEATURE']
                    }
                }
            }
        """

        when:
        def result = run("herokuEnableFeaturesFor${APP_NAME.capitalize()}")

        then:
        result.output.contains("Enabled features for $APP_NAME")
        result.task(":herokuEnableFeaturesFor${APP_NAME.capitalize()}").outcome == SUCCESS

        and:
        herokuClient.getFeature(APP_NAME, FEATURE).enabled
        herokuClient.getFeature(APP_NAME, ANOTHER_FEATURE).enabled
    }

}
