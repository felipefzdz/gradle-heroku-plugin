package com.felipefzdz.gradle.heroku

import com.felipefzdz.gradle.heroku.heroku.DefaultHerokuClient
import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.spock.WiremockScenario
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.junit.WireMockRule
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Shared
import spock.lang.Specification

import static com.github.tomakehurst.wiremock.client.WireMock.recordSpec
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options

@WiremockScenario(
        resetRecordIf = { Boolean.valueOf(System.getenv('GRADLE_HEROKU_PLUGIN_RECORD_SCENARIOS')) },
        ports = [8080, 8081],
        targets = ['https://api.heroku.com', 'https://functional-test-app.herokuapp.com'],
        mappingsParentFolder = 'src/functTest/resources/'
)
abstract class BaseFuncTest extends Specification {

    public static final String APP_NAME = 'functional-test-app'

    @Shared
    HerokuClient herokuClient = new DefaultHerokuClient().init(System.getenv("GRADLE_HEROKU_PLUGIN_API_KEY"))

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
