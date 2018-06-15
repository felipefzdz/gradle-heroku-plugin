package com.felipefzdz.gradle.heroku

import static com.felipefzdz.gradle.heroku.utils.FormatUtil.toUpperCamel
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class InstallAddonsFuncTest extends BaseFuncTest {

    String APP_NAME = 'functional-test-app'
    String ANOTHER_APP_NAME = 'another-functional-test-app'

    @Override
    def getSubjectPlugin() {
        'heroku.base'
    }

    @Override
    def getMappingsDirectory() {
        'installAddons'
    }

    def cleanup() {
        herokuClient.destroyApp(APP_NAME)
    }

    def "can install addons for an app"() {
        given:
        herokuClient.createApp(APP_NAME, 'test', true, 'cedar-14')
        buildFile << """
            import com.felipefzdz.gradle.heroku.tasks.model.HerokuWebApp

            heroku {
                bundles {
                    dev {
                        '$APP_NAME'(HerokuWebApp) {
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
                        '$ANOTHER_APP_NAME'(HerokuWebApp) {
                            teamName = 'test'
                            stack = 'heroku-16'
                            personalApp = true
                        }
                    }
                }
            }
        """

        when:
        def result = run("herokuInstallAddonsForDev${toUpperCamel(APP_NAME)}")

        then:
        result.output.contains("Successfully installed addon DATABASE")
        result.task(":herokuInstallAddonsForDev${toUpperCamel(APP_NAME)}").outcome == SUCCESS

        and:
        herokuClient.getAddonAttachments(APP_NAME)*.name == ['DATABASE']
    }
}
