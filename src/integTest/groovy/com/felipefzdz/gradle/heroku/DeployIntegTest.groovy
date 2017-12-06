package com.felipefzdz.gradle.heroku

import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.Deploy
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.Subject

class DeployIntegTest extends Specification {

    @Rule
    TemporaryFolder temporaryFolder = new TemporaryFolder()

    HerokuClient herokuClient = Mock(HerokuClient)

    @Subject
    Deploy deployer

    String API_KEY = 'apiKey'
    String APP_NAME = 'appName'
    String TEAM_NAME = 'teamName'
    Boolean PERSONAL_APP = true

    def setup() {
        def project = ProjectBuilder.builder().withProjectDir(temporaryFolder.root).build()
        deployer = project.tasks.create('deployer', Deploy)
        deployer.herokuClient = herokuClient
        deployer.apiKey = API_KEY
        deployer.appName = APP_NAME
        deployer.teamName = TEAM_NAME
        deployer.personalApp = PERSONAL_APP
        deployer.recreate = false
    }


    def "create an app when missing"() {
        given:
        herokuClient.appExists(APP_NAME) >> false

        when:
        deployer.deploy()

        then:
        1 * herokuClient.createApp(APP_NAME, TEAM_NAME, PERSONAL_APP)
    }

    def "skip creating an app when already exists"() {
        given:
        herokuClient.appExists(APP_NAME) >> true

        when:
        deployer.deploy()

        then:
        0 * herokuClient.createApp(APP_NAME, TEAM_NAME, PERSONAL_APP)
    }

    def "destroy and create an app when recreate"() {
        given:
        deployer.recreate = true
        deployer.delayAfterDestroyApp = 0
        herokuClient.appExists(APP_NAME) >> true

        when:
        deployer.deploy()

        then:
        1 * herokuClient.destroyApp(APP_NAME)
        1 * herokuClient.createApp(APP_NAME, TEAM_NAME, PERSONAL_APP)
    }

    def "skip destroying an app when missing"() {
        given:
        deployer.recreate = true
        herokuClient.appExists(APP_NAME) >> false

        when:
        deployer.deploy()

        then:
        0 * herokuClient.destroyApp(APP_NAME)
    }


}
