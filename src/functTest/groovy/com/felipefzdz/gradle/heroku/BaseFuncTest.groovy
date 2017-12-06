package com.felipefzdz.gradle.heroku

import com.felipefzdz.gradle.heroku.heroku.DefaultHerokuClient
import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

abstract class BaseFuncTest extends Specification {

    public static final String GRADLE_HEROKU_PLUGIN_API_KEY = System.getenv('GRADLE_HEROKU_PLUGIN_API_KEY')

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
