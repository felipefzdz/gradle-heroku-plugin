package com.felipefzdz.gradle.heroku

import spock.lang.Requires

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

@Requires({
    GRADLE_HEROKU_PLUGIN_API_KEY && !GRADLE_HEROKU_PLUGIN_API_KEY.equals('null')
})
class DeployBundleFuncTest extends BaseFuncTest {

    String APP_NAME = 'functional-test-app'
    String ANOTHER_APP_NAME = 'another-functional-test-app'
    String LOG_DRAIN_URL = 'syslog://logs.example.com'
    String ANOTHER_LOG_DRAIN_URL = 'syslog://another-logs.example.com'
    String FEATURE = 'http-session-affinity'

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
            import com.felipefzdz.gradle.heroku.tasks.model.HerokuWebApp

            heroku {
                apiKey = '$GRADLE_HEROKU_PLUGIN_API_KEY'
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
                        logDrains = ['$LOG_DRAIN_URL', '$ANOTHER_LOG_DRAIN_URL']
                        build {
                            buildpackUrl = 'https://codon-buildpacks.s3.amazonaws.com/buildpacks/heroku/jvm-common.tgz'
                            buildUrl = 'https://github.com/ratpack/ratpack/archive/v1.1.1.tar.gz'
                            buildVersion = '666'
                        }
                        config = ['MODE': 'dev', 'API_KEY': 'secret']
                        features = ['$FEATURE']
                    }
                    '$ANOTHER_APP_NAME'(HerokuWebApp) {
                        teamName = 'test'
                        stack = 'heroku-16'
                        personalApp = true
                        addons {
                            'rabbitmq-bigwig' {
                                plan = 'rabbitmq-bigwig:pipkin'
                                waitUntilStarted = true
                            } 
                        }
                        addonAttachments {
                            database {
                                owningApp = '$APP_NAME'
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
        herokuClient.getAddonAttachments(ANOTHER_APP_NAME)*.name.containsAll(['RABBITMQ_BIGWIG', 'DATABASE'])

        and:
        herokuClient.listLogDrains(APP_NAME)*.url.containsAll([LOG_DRAIN_URL, ANOTHER_LOG_DRAIN_URL])

        and:
        herokuClient.listBuilds(APP_NAME)*.status == ['succeeded']

        and:
        def config = herokuClient.listConfig(APP_NAME)
        config.keySet().containsAll(['MODE', 'API_KEY'])
        config.values().containsAll(['dev', 'secret'])

        and:
        herokuClient.getFeature(APP_NAME, FEATURE).enabled
    }

}
