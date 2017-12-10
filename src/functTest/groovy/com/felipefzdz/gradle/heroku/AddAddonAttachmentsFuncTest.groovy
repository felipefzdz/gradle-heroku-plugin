package com.felipefzdz.gradle.heroku

import spock.lang.Requires

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

@Requires({
    GRADLE_HEROKU_PLUGIN_API_KEY && !GRADLE_HEROKU_PLUGIN_API_KEY.equals('null')
})
class AddAddonAttachmentsFuncTest extends BaseFuncTest {

    String OWNING_APP_NAME = 'owning-functional-test-app'
    String APP_NAME = 'functional-test-app'

    @Override
    def getSubjectPlugin() {
        'heroku-base'
    }

    def cleanup() {
        herokuClient.destroyApp(APP_NAME)
        herokuClient.destroyApp(OWNING_APP_NAME)
    }

    def "can add addon attachments for an app"() {
        given:
        herokuClient.createApp(OWNING_APP_NAME, 'test', true, 'cedar-14')
        herokuClient.installAddon(OWNING_APP_NAME, 'heroku-postgresql:hobby-dev')
        herokuClient.createApp(APP_NAME, 'test', true, 'cedar-14')

        buildFile << """
            import com.felipefzdz.gradle.heroku.tasks.model.HerokuWebApp

            heroku {
                apiKey = '$GRADLE_HEROKU_PLUGIN_API_KEY'
                bundle {
                    '$OWNING_APP_NAME'(HerokuWebApp) {
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
        """

        when:
        def result = run("herokuAddAddonAttachmentsFor${APP_NAME.capitalize()}")

        then:
        result.output.contains("Successfully added addon attachment DATABASE")
        result.task(":herokuAddAddonAttachmentsFor${APP_NAME.capitalize()}").outcome == SUCCESS

        and:
        herokuClient.getAddonAttachments(APP_NAME)*.name == ['DATABASE']
    }
}
