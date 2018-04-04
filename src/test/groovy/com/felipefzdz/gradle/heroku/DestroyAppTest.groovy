package com.felipefzdz.gradle.heroku

import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.services.DestroyAppService
import spock.lang.Specification

class DestroyAppTest extends Specification {

    DestroyAppService destroyAppService

    HerokuClient herokuClient = Mock(HerokuClient)

    String APP_NAME = 'appName'

    def setup() {
        destroyAppService = new DestroyAppService(herokuClient)
    }

    def "skip destroying an app when missing"() {
        given:
        herokuClient.appExists(APP_NAME) >> false

        when:
        destroyAppService.destroyApp(APP_NAME)

        then:
        0 * herokuClient.destroyApp(APP_NAME)
    }

    def "destroy an app when already exists"() {
        given:
        herokuClient.appExists(APP_NAME) >> true

        when:
        destroyAppService.destroyApp(APP_NAME)

        then:
        1 * herokuClient.destroyApp(APP_NAME)
    }

}
