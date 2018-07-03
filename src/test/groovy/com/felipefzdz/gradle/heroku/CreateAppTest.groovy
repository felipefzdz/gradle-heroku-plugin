package com.felipefzdz.gradle.heroku

import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.model.HerokuApp
import com.felipefzdz.gradle.heroku.tasks.services.CreateAppService
import spock.lang.Specification

class CreateAppTest extends Specification {

    CreateAppService createAppService
    HerokuApp app
    HerokuClient herokuClient = Mock(HerokuClient)

    String APP_NAME = 'appName'
    String TEAM_NAME = 'teamName'
    Boolean PERSONAL_APP = true
    String STACK = 'cedar-14'

    def setup() {
        createAppService = new CreateAppService(herokuClient, new NoOpLogger('test'))

        app = new HerokuApp(APP_NAME, null)
        app.teamName = TEAM_NAME
        app.personalApp = PERSONAL_APP
        app.stack = STACK
    }


    def "create an app when missing"() {
        given:
        herokuClient.appExists(APP_NAME) >> false

        when:
        createAppService.createApp(app, APP_NAME)

        then:
        1 * herokuClient.createApp(APP_NAME, TEAM_NAME, PERSONAL_APP, STACK)
    }

    def "skip creating an app when already exists"() {
        given:
        herokuClient.appExists(APP_NAME) >> true

        when:
        createAppService.createApp(app, APP_NAME)

        then:
        0 * herokuClient.createApp(APP_NAME, TEAM_NAME, PERSONAL_APP, STACK)
    }

}
