package com.felipefzdz.gradle.heroku

import static com.felipefzdz.gradle.heroku.utils.FormatUtil.toUpperCamel
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class AddAddonAttachmentsFuncTest extends BaseFuncTest {

    String OWNING_APP_NAME = 'owning-functional-test-app'
    String APP_NAME = 'functional-test-app'

    @Override
    def getSubjectPlugin() {
        'heroku.base'
    }

    @Override
    def getMappingsDirectory() {
        'addAddonAttachments'
    }

    def cleanup() {
        herokuClient.destroyApp(APP_NAME)
        herokuClient.destroyApp(OWNING_APP_NAME)
    }

    def "can add addon attachments for an app"() {
        given:
        herokuClient.createApp(OWNING_APP_NAME, 'test', true, 'cedar-14')
        herokuClient.installAddon(OWNING_APP_NAME, 'heroku-postgresql:hobby-dev', ['version': '10.7'])
        herokuClient.createApp(APP_NAME, 'test', true, 'cedar-14')

        buildFile << """
            import com.felipefzdz.gradle.heroku.tasks.model.HerokuWebApp

            heroku {
                bundles {
                    dev {
                        '$OWNING_APP_NAME'(HerokuWebApp) {
                            teamName = 'test'
                            stack = 'cedar-14'
                            personalApp = true
                            addons {
                                database {
                                    plan = 'heroku-postgresql:hobby-dev'
                                    config = ['version': '10.7']
                                    waitUntilStarted = true
                                } 
                            }
                        }
                        '$APP_NAME'(HerokuWebApp) {
                            teamName = 'test'
                            stack = 'heroku-16'
                            personalApp = true
                            addonAttachments {
                                database {
                                    owningApp = '$OWNING_APP_NAME'
                                } 
                            }
                        }
                    }
                }
            }
        """

        when:
        def result = run("herokuAddAddonAttachmentsForDev${toUpperCamel(APP_NAME)}")

        then:
        result.output.contains("Successfully added addon attachment DATABASE")
        result.task(":herokuAddAddonAttachmentsForDev${toUpperCamel(APP_NAME)}").outcome == SUCCESS

        and:
        herokuClient.getAddonAttachments(APP_NAME)*.name == ['DATABASE']
    }
}
