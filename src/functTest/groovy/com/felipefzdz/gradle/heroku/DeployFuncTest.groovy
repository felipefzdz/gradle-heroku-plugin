package com.felipefzdz.gradle.heroku

import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Requires
import spock.lang.Specification

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

@Requires({
    GRADLE_HEROKU_PLUGIN_API_KEY && !GRADLE_HEROKU_PLUGIN_API_KEY.equals('null')
})
class DeployFuncTest extends Specification {

    public static final String GRADLE_HEROKU_PLUGIN_API_KEY = System.getenv('GRADLE_HEROKU_PLUGIN_API_KEY')

    @Rule
    TemporaryFolder testProjectDir = new TemporaryFolder()
    File buildFile

    def setup() {
        buildFile = testProjectDir.newFile('build.gradle')
        buildFile << """
            plugins {
                id 'com.felipefzdz.gradle.heroku'
            }
        """
    }

    def cleanup() {
        GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments('herokuDestroyApp', '--stacktrace')
            .withDebug(true)
            .withPluginClasspath()
            .forwardOutput()
            .build()
    }

    def "can deploy an app"() {
        buildFile << """
            heroku {
                apiKey = '$GRADLE_HEROKU_PLUGIN_API_KEY'
                appName = 'functional-test-app'
                teamName = 'test'
                personalApp = true
            }
        """

        when:
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments('herokuDeploy', '--stacktrace')
                .withDebug(true)
                .withPluginClasspath()
                .forwardOutput()
                .build()

        then:
        result.output.contains("Successfully deployed app functional-test-app")
        result.task(":herokuDeploy").outcome == SUCCESS

    }
}
