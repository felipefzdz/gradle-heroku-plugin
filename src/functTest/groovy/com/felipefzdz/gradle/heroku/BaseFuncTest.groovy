package com.felipefzdz.gradle.heroku

import com.felipefzdz.gradle.heroku.heroku.DefaultHerokuClient
import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.junit.WireMockRule
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Shared
import spock.lang.Specification

import static com.github.tomakehurst.wiremock.client.WireMock.recordSpec
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options

abstract class BaseFuncTest extends Specification {

    public static final String APP_NAME = 'functional-test-app'
    public static
    final boolean IS_RECORD_SCENARIOS = System.getenv('GRADLE_HEROKU_PLUGIN_RECORD_SCENARIOS') != null && System.getenv('GRADLE_HEROKU_PLUGIN_RECORD_SCENARIOS') == 'ENABLED'

    @Shared
    WireMockServer herokuApiServer

    @Shared
    WireMockServer appServer

    @Shared
    HerokuClient herokuClient = new DefaultHerokuClient().init(System.getenv("GRADLE_HEROKU_PLUGIN_API_KEY"))

    def setupSpec() {
        if (IS_RECORD_SCENARIOS) {
            herokuApiServer = new WireMockServer(options().port(8080))
            herokuApiServer.start()
            herokuApiServer.startRecording(recordSpec().forTarget('https://api.heroku.com'))
            appServer = new WireMockRule(options().port(8081))
            appServer.start()
            appServer.startRecording(recordSpec().forTarget("https://${APP_NAME}.herokuapp.com"))
        } else {
            herokuApiServer = new WireMockServer(options().usingFilesUnderDirectory("src/functTest/resources/$mappingsDirectory"))
            herokuApiServer.start()
        }
    }

    def cleanupSpec() {
        if (IS_RECORD_SCENARIOS) {
            appServer.stopRecording()
            appServer.stop()
            herokuApiServer.stopRecording()
        }
        herokuApiServer.stop()
    }

    @Rule
    TemporaryFolder testProjectDir = new TemporaryFolder()

    File buildFile


    def setup() {
        buildFile = testProjectDir.newFile('build.gradle')
        buildFile << """
            plugins {
                id 'com.felipefzdz.gradle.$subjectPlugin'
            }
        """
    }

    def getMappingsDirectory() { '' }

    abstract def getSubjectPlugin()

    def runAndFail(String task) {
        run(task, true)
    }

    def run(String task, boolean fail = false) {
        def r = runner(task)
        fail ? r.buildAndFail() : r.build()
    }

    private GradleRunner runner(String task) {
        GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments(task, '--stacktrace')
                .withDebug(true)
                .withPluginClasspath()
                .forwardOutput()
    }

}
