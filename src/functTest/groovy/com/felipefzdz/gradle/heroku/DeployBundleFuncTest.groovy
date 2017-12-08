package com.felipefzdz.gradle.heroku

import spock.lang.Requires

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

@Requires({
    GRADLE_HEROKU_PLUGIN_API_KEY && !GRADLE_HEROKU_PLUGIN_API_KEY.equals('null')
})
class DeployBundleFuncTest extends BaseFuncTest {

    String APP_NAME = 'functional-test-app'
    String ANOTHER_APP_NAME = 'another-functional-test-app'

    @Override
    def getSubjectPlugin() {
        'heroku'
    }

    def cleanup() {
        herokuClient.destroyApp(APP_NAME)
        herokuClient.destroyApp(ANOTHER_APP_NAME)
    }

    def "can deploy an app"() {
        given:
        buildFile << """
            heroku {
                apiKey = '$GRADLE_HEROKU_PLUGIN_API_KEY'
                bundle {
                    '$APP_NAME' {
                        teamName = 'test'
                        stack = 'heroku-16'
                        personalApp = true
                        addons {
                            database {
                                plan = 'heroku-postgresql:hobby-dev'
                                waitUntilStarted = true
                            } 
                        }
                    }
                    '$ANOTHER_APP_NAME' {
                        teamName = 'test'
                        stack = 'heroku-16'
                        personalApp = true
                        addons {
                            'rabbitmq-bigwig' {
                                plan = 'rabbitmq-bigwig:pipkin'
                                waitUntilStarted = true
                            } 
                        }
                    }
                }
            }
        """

        when:
        def result = run("herokuDeployBundle")

        then:
        result.output.contains("Successfully deployed app $APP_NAME")
        result.task(":herokuDeployBundle").outcome == SUCCESS

        and:
        herokuClient.appExists(APP_NAME)
        herokuClient.appExists(ANOTHER_APP_NAME)

        and:
        herokuClient.getAddonAttachments(APP_NAME)*.name == ['DATABASE']
        herokuClient.getAddonAttachments(ANOTHER_APP_NAME)*.name == ['RABBITMQ_BIGWIG']
    }

}
