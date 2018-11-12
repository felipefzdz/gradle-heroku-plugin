package com.felipefzdz.gradle.heroku

import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.services.ConfigureLogDrainsService
import com.heroku.api.exception.RequestFailedException
import spock.lang.Specification

import static com.felipefzdz.gradle.heroku.tasks.services.ConfigureLogDrainsService.RECOVERABLE_ADD_LOG_DRAIN_EXCEPTION

class ConfigureLogDrainsTest extends Specification {

    ConfigureLogDrainsService configureLogDrainsService
    List<String> logDrains

    HerokuClient herokuClient = Mock(HerokuClient)

    String APP_NAME = 'appName'

    def setup() {
        configureLogDrainsService = new ConfigureLogDrainsService(herokuClient, new NoOpLogger('test'), 10)
        logDrains = ['test1', 'test2']
    }

    def "add log drains only when missing"() {
        given:
        herokuClient.listLogDrains(APP_NAME) >> [['url': 'test1']]

        when:
        configureLogDrainsService.configureLogDrains(logDrains, APP_NAME, 1)

        then:
        0 * herokuClient.addLogDrain(APP_NAME, 'test1')
        1 * herokuClient.addLogDrain(APP_NAME, 'test2')
    }

    def "retry when getting known race condition"() {
        given:
        herokuClient.listLogDrains(APP_NAME) >> [[:]]
        4 * herokuClient.addLogDrain(APP_NAME, 'test1') >> { String appName, String logDrain ->
            throw new RequestFailedException("Exception", 408, RECOVERABLE_ADD_LOG_DRAIN_EXCEPTION)
        }

        when:
        configureLogDrainsService.configureLogDrains(['test1'], APP_NAME, 3)

        then:
        thrown(RequestFailedException)
    }

    def "do not retry when not getting known race condition"() {
        given:
        herokuClient.listLogDrains(APP_NAME) >> [[:]]
        1 * herokuClient.addLogDrain(APP_NAME, 'test1') >> { String appName, String logDrain ->
            throw new RequestFailedException("Exception", 408, 'Something else')
        }

        when:
        configureLogDrainsService.configureLogDrains(['test1'], APP_NAME, 3)

        then:
        thrown(RequestFailedException)
    }

}
