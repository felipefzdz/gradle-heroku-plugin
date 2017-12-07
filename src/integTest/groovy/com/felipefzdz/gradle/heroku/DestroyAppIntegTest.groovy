package com.felipefzdz.gradle.heroku

import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.DestroyApp
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.Subject

class DestroyAppIntegTest extends Specification {

    @Rule
    TemporaryFolder temporaryFolder = new TemporaryFolder()

    HerokuClient herokuClient = Mock(HerokuClient)

    @Subject
    DestroyApp destroyApp

    String API_KEY = 'apiKey'
    String APP_NAME = 'appName'

    def setup() {
        def project = ProjectBuilder.builder().withProjectDir(temporaryFolder.root).build()
        destroyApp = project.tasks.create('destroyApp', DestroyApp)
        destroyApp.herokuClient = herokuClient
        destroyApp.apiKey = API_KEY
        destroyApp.appName = APP_NAME
    }

    def "skip destroying an app when missing"() {
        given:
        herokuClient.appExists(APP_NAME) >> false

        when:
        destroyApp.destroyApp()

        then:
        0 * herokuClient.destroyApp(APP_NAME)
    }

    def "destroy an app when already exists"() {
        given:
        herokuClient.appExists(APP_NAME) >> true

        when:
        destroyApp.destroyApp()

        then:
        1 * herokuClient.destroyApp(APP_NAME)
    }

}
