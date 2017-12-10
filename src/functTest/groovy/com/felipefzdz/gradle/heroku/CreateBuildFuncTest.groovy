package com.felipefzdz.gradle.heroku

import spock.lang.Requires

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

@Requires({
    GRADLE_HEROKU_PLUGIN_API_KEY && !GRADLE_HEROKU_PLUGIN_API_KEY.equals('null')
})
class CreateBuildFuncTest extends BaseFuncTest {

    String APP_NAME = 'functional-test-app'

    @Override
    def getSubjectPlugin() {
        'heroku-base'
    }

    def cleanup() {
        herokuClient.destroyApp(APP_NAME)
    }

    def "can create a build for an app"() {
        given:
        herokuClient.createApp(APP_NAME, 'test', true, 'cedar-14')
        buildFile << """
            import com.felipefzdz.gradle.heroku.tasks.model.HerokuWebApp

            heroku {
                apiKey = '$GRADLE_HEROKU_PLUGIN_API_KEY'
                bundle {
                    '$APP_NAME'(HerokuWebApp) {
                        build {
                            buildpackUrl = 'https://codon-buildpacks.s3.amazonaws.com/buildpacks/heroku/jvm-common.tgz'
                            buildUrl = 'https://github.com/ratpack/ratpack/archive/v1.1.1.tar.gz'
                            buildVersion = '666'
                        }
                    }
                }
            }
        """

        when:
        def result = run("herokuCreateBuildFor${APP_NAME.capitalize()}")

        then:
        result.output.contains("Created build for $APP_NAME")
        result.task(":herokuCreateBuildFor${APP_NAME.capitalize()}").outcome == SUCCESS

        and:
        herokuClient.listBuilds(APP_NAME)*.status == ['succeeded']
    }

}
