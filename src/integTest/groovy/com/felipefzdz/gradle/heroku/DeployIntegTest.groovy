package com.felipefzdz.gradle.heroku

import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.Deploy
import com.felipefzdz.gradle.heroku.tasks.InstallAddonsService
import com.felipefzdz.gradle.heroku.tasks.model.HerokuAddon
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
    Deploy deploy

    String API_KEY = 'apiKey'
    String APP_NAME = 'appName'
    String TEAM_NAME = 'teamName'
    Boolean PERSONAL_APP = true
    String PLAN = 'heroku-redis:hobby-dev'

    def setup() {
        def project = ProjectBuilder.builder().withProjectDir(temporaryFolder.root).build()
        deploy = project.tasks.create('deploy', Deploy)

        deploy.herokuClient = herokuClient
        deploy.installAddonsService = new InstallAddonsService(herokuClient)
        deploy.apiKey = API_KEY
        deploy.appName = APP_NAME
        deploy.teamName = TEAM_NAME
        deploy.personalApp = PERSONAL_APP
        deploy.recreate = false
        def redisAddon = new HerokuAddon('redis')
        redisAddon.plan = PLAN
        deploy.addons = [redisAddon]

        herokuClient.getAddonAttachments(APP_NAME) >> []
    }


    def "create an app when missing"() {
        given:
        herokuClient.appExists(APP_NAME) >> false

        when:
        deploy.deploy()

        then:
        1 * herokuClient.createApp(APP_NAME, TEAM_NAME, PERSONAL_APP)
    }

    def "skip creating an app when already exists"() {
        given:
        herokuClient.appExists(APP_NAME) >> true

        when:
        deploy.deploy()

        then:
        0 * herokuClient.createApp(APP_NAME, TEAM_NAME, PERSONAL_APP)
    }

    def "destroy and create an app when recreate"() {
        given:
        deploy.recreate = true
        deploy.delayAfterDestroyApp = 0
        herokuClient.appExists(APP_NAME) >> true

        when:
        deploy.deploy()

        then:
        1 * herokuClient.destroyApp(APP_NAME)
        1 * herokuClient.createApp(APP_NAME, TEAM_NAME, PERSONAL_APP)
    }

    def "skip destroying an app when missing"() {
        given:
        deploy.recreate = true
        herokuClient.appExists(APP_NAME) >> false

        when:
        deploy.deploy()

        then:
        0 * herokuClient.destroyApp(APP_NAME)
    }


}
