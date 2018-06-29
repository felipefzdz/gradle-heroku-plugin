package com.felipefzdz.gradle.heroku

import static com.felipefzdz.gradle.heroku.utils.FormatUtil.toUpperCamel
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class DeployWebFuncTest extends BaseFuncTest {

    String APP_NAME = 'functional-test-app'

    @Override
    def getMappingsDirectory() {
        'deployWeb'
    }

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
                        }
                    }
                }
            }
        """

        when:
        def result = run("herokuDeployDev${toUpperCamel(APP_NAME)}")

        then:
        result.output.contains("Successfully deployed app $APP_NAME")
        result.task(":herokuDeployDev${toUpperCamel(APP_NAME)}").outcome == SUCCESS

        and:
        herokuClient.appExists(APP_NAME)

        and:
        herokuClient.getAddonAttachments(APP_NAME)*.name == ['DATABASE']
    }

}
