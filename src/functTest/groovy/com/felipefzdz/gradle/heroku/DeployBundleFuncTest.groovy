package com.felipefzdz.gradle.heroku

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class DeployBundleFuncTest extends BaseFuncTest {

    String DATABASE_APP_NAME = 'database-functional-test-app'
    String EXCLUDED_DATABASE_APP_NAME = 'excluded-database-functional-test-app'
    String LOG_DRAIN_URL = 'syslog://logs.example.com'
    String ANOTHER_LOG_DRAIN_URL = 'syslog://another-logs.example.com'
    String FEATURE = 'http-session-affinity'
    String FIRST_DOMAIN = 'my.domain.com'
    String SECOND_DOMAIN = 'my.domain.org'

    @Override
    def getSubjectPlugin() {
        'heroku'
    }

    @Override
    def getMappingsDirectory() {
        'deployBundle'
    }

    def cleanup() {
        herokuClient.destroyApp(APP_NAME)
        herokuClient.destroyApp(DATABASE_APP_NAME)
    }

    def "can deploy a bundle"() {
        given:
        buildFile << """
            import com.felipefzdz.gradle.heroku.tasks.model.HerokuWebApp
            import com.felipefzdz.gradle.heroku.tasks.model.HerokuDatabaseApp

            heroku {
                bundles {
                    dev {
                        '$DATABASE_APP_NAME'(HerokuDatabaseApp) {
                            teamName = 'test'
                            stack = 'heroku-16'
                            personalApp = true
                            addons {
                                database {
                                    plan = 'heroku-postgresql:hobby-dev'
                                    waitUntilStarted = true
                                } 
                            }
                            migrateCommand = 'bash'
                        }
                        '$EXCLUDED_DATABASE_APP_NAME'(HerokuDatabaseApp) {
                            exclude = true
                            teamName = 'test'
                            stack = 'heroku-16'
                            personalApp = true
                            addons {
                                database {
                                    plan = 'heroku-postgresql:hobby-dev'
                                    waitUntilStarted = true
                                } 
                            }
                            migrateCommand = 'bash'
                        }
                        '$APP_NAME'(HerokuWebApp) {
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
                                    owningApp = '$DATABASE_APP_NAME'
                                } 
                            }
                            logDrains = ['$LOG_DRAIN_URL', '$ANOTHER_LOG_DRAIN_URL']
                            buildSource {
                                buildpackUrl = 'https://codon-buildpacks.s3.amazonaws.com/buildpacks/heroku/jvm-common.tgz'
                                buildUrl = { 'https://www.dropbox.com/s/i2unpznuqztuvp2/example.tgz' }
                                buildVersion = '666'
                            }
                            config {
                                configToBeExpected = ['MODE': 'dev', 'API_KEY': 'secret']
                            }
                            features = ['$FEATURE']
                            process {
                                type = 'web'
                                size = 'standard-1x'
                                quantity = 2
                            }
                            readinessProbe {
                                url = 'https://${APP_NAME}.herokuapp.com/version'
                                command = { app, json ->
                                    assert json.buildNumber == app.buildSource.buildVersion 
                                }
                            }
                            disableAcm = true
                            domains = ['$FIRST_DOMAIN', '$SECOND_DOMAIN'] 
                        }
                    }
                }
            }
        """

        when:
        def result = run("herokuDeployDevBundle")

        then:
        result.output.contains("Successfully deployed app $DATABASE_APP_NAME")
        result.output.contains("Successfully deployed app $APP_NAME")
        result.task(":herokuDeployDevBundle").outcome == SUCCESS

        and:
        herokuClient.appExists(DATABASE_APP_NAME)
        herokuClient.appExists(APP_NAME)

        and:
        herokuClient.getAddonAttachments(DATABASE_APP_NAME)*.name == ['DATABASE']
        herokuClient.getAddonAttachments(APP_NAME)*.name.containsAll(['RABBITMQ_BIGWIG', 'DATABASE'])

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

        and:
        herokuClient.getFormations(APP_NAME)*.type == ['web']
        herokuClient.getFormations(APP_NAME)*.size == ['Standard-1X']
        herokuClient.getFormations(APP_NAME)*.quantity == [2]

        and:
        herokuClient.getCustomDomains(APP_NAME).containsAll([FIRST_DOMAIN, SECOND_DOMAIN])

        and:
        !herokuClient.getApp(APP_NAME).acm
    }

}
