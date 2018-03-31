package com.felipefzdz.gradle.heroku

import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.model.HerokuAddon
import com.felipefzdz.gradle.heroku.tasks.services.InstallAddonsService
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

class InstallAddonsTest extends Specification {

    InstallAddonsService installAddonsService
    List<HerokuAddon> addons

    @Shared
    @AutoCleanup
    ServerSocket serverSocket = new ServerSocket(0)

    HerokuClient herokuClient = Mock(HerokuClient)

    String API_KEY = 'apiKey'
    String APP_NAME = 'appName'
    String PLAN = 'heroku-redis:hobby-dev'

    def setup() {
        installAddonsService = new InstallAddonsService(herokuClient)

        def redisAddon = new HerokuAddon('redis')
        redisAddon.plan = PLAN
        redisAddon.waitUntilStarted = true
        addons = [redisAddon]
    }

    def "install an addon when missing"() {
        given:
        herokuClient.getAddonAttachments(APP_NAME) >> []
        herokuClient.listConfig(APP_NAME) >> ['REDIS_URL': "http://127.0.0.1:${serverSocket.localPort}"]

        when:
        installAddonsService.installAddons(addons, API_KEY, APP_NAME)

        then:
        1 * herokuClient.installAddon(APP_NAME, PLAN)
    }

    def "skip installing an addon when already exists"() {
        given:
        herokuClient.getAddonAttachments(APP_NAME) >> [['name': 'REDIS']]

        when:
        installAddonsService.installAddons(addons, API_KEY, APP_NAME)

        then:
        0 * herokuClient.installAddon(APP_NAME, PLAN)
    }
}
