package com.felipefzdz.gradle.heroku

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
        run("herokuDeployDev${toUpperCamel(APP_NAME)}")
    }

    def "can verify different config states"() {
        when:
        def result = run("herokuVerifyConfigForDev${toUpperCamel(APP_NAME)}")

        then:
        result.output.contains("Verified config for $APP_NAME")
        result.task(":herokuVerifyConfigForDev${toUpperCamel(APP_NAME)}").outcome == SUCCESS

        when:
        buildFileWith(configToBeExpected: ['MODE': 'dev', 'AUDIENCE': 'public'])
        result = runAndFail("herokuVerifyConfigForDev${toUpperCamel(APP_NAME)}")

        then:
        result.output.contains("Expected config missing for $APP_NAME")
        result.task(":herokuVerifyConfigForDev${toUpperCamel(APP_NAME)}").outcome == FAILED

        when:
        buildFileWith(configToBeExpected: ['MODE': 'dev', 'AUDIENCE': 'public'], configToBeAdded: ['AUDIENCE'])
        result = run("herokuVerifyConfigForDev${toUpperCamel(APP_NAME)}")

        then:
        result.output.contains("Verified config for $APP_NAME")
        result.task(":herokuVerifyConfigForDev${toUpperCamel(APP_NAME)}").outcome == SUCCESS

        when:
        run("herokuAddEnvironmentConfigForDev${toUpperCamel(APP_NAME)}")
        buildFileWith(configToBeExpected: ['MODE': 'dev', 'AUDIENCE': 'public'])
        result = run("herokuVerifyConfigForDev${toUpperCamel(APP_NAME)}")

        then:
        result.output.contains("Verified config for $APP_NAME")
        result.task(":herokuVerifyConfigForDev${toUpperCamel(APP_NAME)}").outcome == SUCCESS

        when:
        buildFileWith(configToBeExpected: ['MODE': null, 'AUDIENCE': 'public'])
        result = runAndFail("herokuVerifyConfigForDev${toUpperCamel(APP_NAME)}")

        then:
        result.output.contains("Unexpected config found for $APP_NAME")
        result.task(":herokuVerifyConfigForDev${toUpperCamel(APP_NAME)}").outcome == FAILED

        when:
        buildFileWith(configToBeExpected: ['MODE': null, 'AUDIENCE': 'public'], configToBeRemoved: ['MODE'])
        result = run("herokuVerifyConfigForDev${toUpperCamel(APP_NAME)}")

        then:
        result.output.contains("Verified config for $APP_NAME")
        result.task(":herokuVerifyConfigForDev${toUpperCamel(APP_NAME)}").outcome == SUCCESS

        when:
        run("herokuAddEnvironmentConfigForDev${toUpperCamel(APP_NAME)}")
        buildFileWith(configToBeExpected: ['AUDIENCE': 'public'])
        result = run("herokuVerifyConfigForDev${toUpperCamel(APP_NAME)}")

        then:
        result.output.contains("Verified config for $APP_NAME")
        result.task(":herokuVerifyConfigForDev${toUpperCamel(APP_NAME)}").outcome == SUCCESS
    }

    private void buildFileWith(config) {
        def configToBeExpected = config.configToBeExpected == null ? [:] : config.configToBeExpected.inspect()
        def configToBeRemoved = config.configToBeRemoved == null ? [] : config.configToBeRemoved.inspect()
        def configToBeAdded = config.configToBeAdded == null ? [] : config.configToBeAdded.inspect()

        buildFile << """
            import com.felipefzdz.gradle.heroku.tasks.model.HerokuWebApp

            heroku {
                bundles {
                    dev {        
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
            }
        """
    }

}
