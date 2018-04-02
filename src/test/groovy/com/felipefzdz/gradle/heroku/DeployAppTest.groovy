package com.felipefzdz.gradle.heroku

import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.model.HerokuApp
import com.felipefzdz.gradle.heroku.tasks.model.HerokuWebApp
import com.felipefzdz.gradle.heroku.tasks.services.*
import spock.lang.Specification

class DeployAppTest extends Specification {

    DeployWebService deployService
    int delayAfterDestroyApp = 20

    HerokuClient herokuClient = Mock(HerokuClient)

    String API_KEY = 'apiKey'
    String APP_NAME = 'appName'
    String TEAM_NAME = 'teamName'
    String STACK = 'cedar-14'
    Boolean PERSONAL_APP = true
    HerokuWebApp app

    def setup() {

        deployService = new DeployWebService(new InstallAddonsService(herokuClient), herokuClient,
                new ConfigureLogDrainsService(herokuClient), new CreateBuildService(herokuClient),
                new EnableFeaturesService(herokuClient), new AddAddonAttachmentsService(herokuClient))

        app = new HerokuWebApp(APP_NAME, deployService, null, null)
        app.teamName = TEAM_NAME
        app.personalApp = PERSONAL_APP
        app.stack = STACK
        app.recreate = false

        herokuClient.getAddonAttachments(APP_NAME) >> []
    }

    def "create an app when missing"() {
        given:
        herokuClient.appExists(APP_NAME) >> false

        when:
        deployService.deploy(app, delayAfterDestroyApp, API_KEY)

        then:
        1 * herokuClient.createApp(APP_NAME, TEAM_NAME, PERSONAL_APP, STACK)
    }

    def "skip creating an app when already exists"() {
        given:
        herokuClient.appExists(APP_NAME) >> true

        when:
        deployService.deploy(app, delayAfterDestroyApp, API_KEY)

        then:
        0 * herokuClient.createApp(APP_NAME, TEAM_NAME, PERSONAL_APP, STACK)
    }

    def "destroy and create an app when recreate"() {
        given:
        app.recreate = true
        herokuClient.appExists(APP_NAME) >> true

        when:
        deployService.deploy(app, 0, API_KEY)

        then:
        1 * herokuClient.destroyApp(APP_NAME)
        1 * herokuClient.createApp(APP_NAME, TEAM_NAME, PERSONAL_APP, STACK)
    }

    def "skip destroying an app when missing"() {
        given:
        app.recreate = true
        herokuClient.appExists(APP_NAME) >> false

        when:
        deployService.deploy(app, delayAfterDestroyApp, API_KEY)

        then:
        0 * herokuClient.destroyApp(APP_NAME)
    }
}
