package com.felipefzdz.gradle.heroku

import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.services.ConfigureLogDrainsService
import spock.lang.Specification

class ConfigureLogDrainsTest extends Specification {

    ConfigureLogDrainsService configureLogDrainsService
    List<String> logDrains

    HerokuClient herokuClient = Mock(HerokuClient)

    String APP_NAME = 'appName'

    def setup() {
        configureLogDrainsService = new ConfigureLogDrainsService(herokuClient, new NoOpLogger('test'))
        logDrains = ['test1', 'test2']
    }

    def "add log drains only when missing"() {
        given:
        herokuClient.listLogDrains(APP_NAME) >> [['url': 'test1']]

        when:
        configureLogDrainsService.configureLogDrains(logDrains, APP_NAME)

        then:
        0 * herokuClient.addLogDrain(APP_NAME, 'test1')
        1 * herokuClient.addLogDrain(APP_NAME, 'test2')
    }

}
