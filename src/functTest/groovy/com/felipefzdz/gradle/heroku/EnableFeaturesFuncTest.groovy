package com.felipefzdz.gradle.heroku

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class EnableFeaturesFuncTest extends BaseFuncTest {

    String APP_NAME = 'functional-test-app'
    String FEATURE = 'http-session-affinity'
    String ANOTHER_FEATURE = 'spaces-dns-discovery'

    @Override
    def getSubjectPlugin() {
        'heroku.base'
    }

    @Override
    def getMappingsDirectory() {
        'enableFeatures'
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
