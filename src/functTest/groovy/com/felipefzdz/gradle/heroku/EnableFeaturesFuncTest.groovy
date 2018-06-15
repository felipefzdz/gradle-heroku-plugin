package com.felipefzdz.gradle.heroku

import static com.felipefzdz.gradle.heroku.utils.FormatUtil.toUpperCamel
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
                bundles {
                    dev {
                        '$APP_NAME'(HerokuWebApp) {
                            features = ['$FEATURE', '$ANOTHER_FEATURE']
                        }
                    }
                }
            }
        """

        when:
        def result = run("herokuEnableFeaturesForDev${toUpperCamel(APP_NAME)}")

        then:
        result.output.contains("Enabled features for $APP_NAME")
        result.task(":herokuEnableFeaturesForDev${toUpperCamel(APP_NAME)}").outcome == SUCCESS

        and:
        herokuClient.getFeature(APP_NAME, FEATURE).enabled
        herokuClient.getFeature(APP_NAME, ANOTHER_FEATURE).enabled
    }

}
