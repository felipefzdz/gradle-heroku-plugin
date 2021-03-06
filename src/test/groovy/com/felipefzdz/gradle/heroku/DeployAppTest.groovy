package com.felipefzdz.gradle.heroku

import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.model.HerokuWebApp
import com.felipefzdz.gradle.heroku.tasks.services.*
import spock.lang.Specification

class DeployAppTest extends Specification {

    DeployWebService deployService
    int delayAfterDestroyApp = 20

    HerokuClient herokuClient = Mock(HerokuClient)

    String APP_NAME = 'appName'
    String TEAM_NAME = 'teamName'
    String STACK = 'cedar-14'
    Boolean PERSONAL_APP = true
    HerokuWebApp app

    def setup() {
        def logger = new NoOpLogger('test')
        deployService = new DeployWebService(new InstallAddonsService(herokuClient, logger), herokuClient,
                new ConfigureLogDrainsService(herokuClient, logger), new CreateBuildService(herokuClient, logger),
                new EnableFeaturesService(herokuClient, logger), new AddAddonAttachmentsService(herokuClient, logger), logger)

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
        deployService.deploy(app, delayAfterDestroyApp)

        then:
        1 * herokuClient.createApp(APP_NAME, TEAM_NAME, PERSONAL_APP, STACK)
    }

    def "skip creating an app when already exists"() {
        given:
        herokuClient.appExists(APP_NAME) >> true

        when:
        deployService.deploy(app, delayAfterDestroyApp)

        then:
        0 * herokuClient.createApp(APP_NAME, TEAM_NAME, PERSONAL_APP, STACK)
    }

    def "destroy and create an app when recreate"() {
        given:
        app.recreate = true
        herokuClient.appExists(APP_NAME) >> true

        when:
        deployService.deploy(app, 0)

        then:
        1 * herokuClient.destroyApp(APP_NAME)
        1 * herokuClient.createApp(APP_NAME, TEAM_NAME, PERSONAL_APP, STACK)
    }

    def "skip destroying an app when missing"() {
        given:
        app.recreate = true
        herokuClient.appExists(APP_NAME) >> false

        when:
        deployService.deploy(app, delayAfterDestroyApp)

        then:
        0 * herokuClient.destroyApp(APP_NAME)
    }
}
