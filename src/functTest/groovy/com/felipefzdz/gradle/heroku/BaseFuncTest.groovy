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
    public static final String GRADLE_HEROKU_PLUGIN_API_KEY = System.getenv('GRADLE_HEROKU_PLUGIN_API_KEY')
    public static
    final boolean IS_RECORD_SCENARIOS = System.getenv('RECORD_SCENARIOS') != null && System.getenv('RECORD_SCENARIOS') == 'ENABLED'

    @Shared
    WireMockServer herokuApiServer

    @Shared
    WireMockServer appServer

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

    HerokuClient herokuClient = new DefaultHerokuClient().init(GRADLE_HEROKU_PLUGIN_API_KEY)

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

    def run(String task) {
        GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments(task, '--stacktrace')
                .withDebug(true)
                .withPluginClasspath()
                .forwardOutput()
                .build()
    }

}
