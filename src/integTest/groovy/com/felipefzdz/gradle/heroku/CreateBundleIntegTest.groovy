package com.felipefzdz.gradle.heroku

import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.CreateBundle
import com.felipefzdz.gradle.heroku.tasks.model.HerokuApp
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.Subject

class CreateBundleIntegTest extends Specification {

    @Rule
    TemporaryFolder temporaryFolder = new TemporaryFolder()

    HerokuClient herokuClient = Mock(HerokuClient)

    @Subject
    CreateBundle createBundle

    String API_KEY = 'apiKey'
    String APP_NAME = 'appName'
    String TEAM_NAME = 'teamName'
    Boolean PERSONAL_APP = true
    String STACK = 'cedar-14'

    def setup() {
        def project = ProjectBuilder.builder().withProjectDir(temporaryFolder.root).build()
        createBundle = project.tasks.create('createBundle', CreateBundle)
        createBundle.herokuClient = herokuClient
        createBundle.apiKey = API_KEY

        def app = new HerokuApp(project)
        app.name = APP_NAME
        app.teamName = TEAM_NAME
        app.personalApp = PERSONAL_APP
        app.stack = STACK
        createBundle.bundle = [app]
    }


    def "create an app when missing"() {
        given:
        herokuClient.appExists(APP_NAME) >> false

        when:
        createBundle.createApp()

        then:
        1 * herokuClient.createApp(APP_NAME, TEAM_NAME, PERSONAL_APP, STACK)
    }

    def "skip creating an app when already exists"() {
        given:
        herokuClient.appExists(APP_NAME) >> true

        when:
        createBundle.createApp()

        then:
        0 * herokuClient.createApp(APP_NAME, TEAM_NAME, PERSONAL_APP, STACK)
    }

}
