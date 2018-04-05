package com.felipefzdz.gradle.heroku

import org.gradle.testkit.runner.TaskOutcome

import static com.felipefzdz.gradle.heroku.utils.FormatUtil.toUpperCamel
import static org.gradle.testkit.runner.TaskOutcome.FAILED
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class VerifyConfigFuncTest extends BaseFuncTest {

    String APP_NAME = 'functional-test-app'
    String FEATURE = 'http-session-affinity'

    @Override
    def getSubjectPlugin() {
        'heroku'
    }

    @Override
    def getMappingsDirectory() {
        'verifyConfig'
    }

    def cleanup() {
        herokuClient.destroyApp(APP_NAME)
    }

    def setup() {
        herokuClient.createApp(APP_NAME, 'test', true, 'cedar-14')
        buildFileWith(configToBeExpected: ['MODE': 'dev'])
        run("herokuDeploy${toUpperCamel(APP_NAME)}")
    }

    def "can verify adding a config value"() {
        when:
        def result = run("herokuVerifyConfigFor${toUpperCamel(APP_NAME)}")

        then:
        result.output.contains("Verified config for $APP_NAME")
        result.task(":herokuVerifyConfigFor${toUpperCamel(APP_NAME)}").outcome == SUCCESS

        when:
        buildFileWith(configToBeExpected: ['MODE': 'dev', 'AUDIENCE': 'public'])
        result = runAndFail("herokuVerifyConfigFor${toUpperCamel(APP_NAME)}")

        then:
        result.output.contains("Expected config missing for $APP_NAME")
        result.task(":herokuVerifyConfigFor${toUpperCamel(APP_NAME)}").outcome == FAILED

        when:
        buildFileWith(configToBeExpected: ['MODE': 'dev', 'AUDIENCE': 'public'], configToBeAdded: ['AUDIENCE'])
        result = run("herokuVerifyConfigFor${toUpperCamel(APP_NAME)}")

        then:
        result.output.contains("Verified config for $APP_NAME")
        result.task(":herokuVerifyConfigFor${toUpperCamel(APP_NAME)}").outcome == SUCCESS

        when:
        run("herokuAddEnvironmentConfigFor${toUpperCamel(APP_NAME)}")
        buildFileWith(configToBeExpected: ['MODE': 'dev', 'AUDIENCE': 'public'])
        result = run("herokuVerifyConfigFor${toUpperCamel(APP_NAME)}")

        then:
        result.output.contains("Verified config for $APP_NAME")
        result.task(":herokuVerifyConfigFor${toUpperCamel(APP_NAME)}").outcome == SUCCESS
    }

    private void buildFileWith(config) {
        def configToBeExpected = config.configToBeExpected == null ? [:] : config.configToBeExpected.inspect()
        def configToBeRemoved = config.configToBeRemoved == null ? [] : config.configToBeRemoved.inspect()
        def configToBeAdded = config.configToBeAdded == null ? [] : config.configToBeAdded.inspect()

        buildFile << """
            import com.felipefzdz.gradle.heroku.tasks.model.HerokuWebApp

            heroku {
                bundle {
                    '$APP_NAME'(HerokuWebApp) {
                        teamName = 'test'
                        stack = 'heroku-16'
                        personalApp = true
                        addons {
                            database {
                                plan = 'heroku-postgresql:hobby-dev'
                                waitUntilStarted = true
                            } 
                        }
                        config {
                            configToBeExpected = $configToBeExpected
                            configToBeRemoved = $configToBeRemoved
                            configToBeAdded = $configToBeAdded
                            configAddedByHeroku = ['DATABASE_URL']
                        }
                        features = ['$FEATURE']
                    }
                }
            }
        """
    }

}
