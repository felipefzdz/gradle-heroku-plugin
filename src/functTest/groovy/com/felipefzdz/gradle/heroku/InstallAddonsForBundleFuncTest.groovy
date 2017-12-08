package com.felipefzdz.gradle.heroku

import spock.lang.Requires

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

@Requires({
    GRADLE_HEROKU_PLUGIN_API_KEY && !GRADLE_HEROKU_PLUGIN_API_KEY.equals('null')
})
class InstallAddonsForBundleFuncTest extends BaseFuncTest {

    String APP_NAME = 'functional-test-app'
    String ANOTHER_APP_NAME = 'another-functional-test-app'

    @Override
    def getSubjectPlugin() {
        'heroku-base'
    }

    def cleanup() {
        herokuClient.destroyApp(APP_NAME)
        herokuClient.destroyApp(ANOTHER_APP_NAME)
    }

    def "can install addons for a bundle"() {
        given:
        herokuClient.createApp(APP_NAME, 'test', true, 'cedar-14')
        herokuClient.createApp(ANOTHER_APP_NAME, 'test', true, 'cedar-14')
        buildFile << """
            heroku {
                apiKey = '$GRADLE_HEROKU_PLUGIN_API_KEY'
                bundle {
                    '$APP_NAME' {
                        teamName = 'test'
                        stack = 'cedar-14'
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
        def result = run("herokuInstallAddonsForBundle")

        then:
        result.output.contains("Successfully installed addon DATABASE")
        result.output.contains("Successfully installed addon RABBITMQ-BIGWIG")
        result.task(":herokuInstallAddonsForBundle").outcome == SUCCESS

        and:
        herokuClient.getAddonAttachments(APP_NAME)*.name == ['DATABASE']
        herokuClient.getAddonAttachments(ANOTHER_APP_NAME)*.name == ['RABBITMQ_BIGWIG']
    }
}
