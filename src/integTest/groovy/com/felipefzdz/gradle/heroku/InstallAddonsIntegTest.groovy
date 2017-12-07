package com.felipefzdz.gradle.heroku

import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.InstallAddons
import com.felipefzdz.gradle.heroku.tasks.InstallAddonsService
import com.felipefzdz.gradle.heroku.tasks.model.HerokuAddon
import com.felipefzdz.gradle.heroku.tasks.model.HerokuApp
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

class InstallAddonsIntegTest extends Specification {

    @Rule
    TemporaryFolder temporaryFolder = new TemporaryFolder()

    @Subject
    InstallAddons installAddons

    @Shared
    @AutoCleanup
    ServerSocket serverSocket = new ServerSocket(0)

    HerokuClient herokuClient = Mock(HerokuClient)

    String API_KEY = 'apiKey'
    String APP_NAME = 'appName'
    String PLAN = 'heroku-redis:hobby-dev'

    def setup() {
        def project = ProjectBuilder.builder().withProjectDir(temporaryFolder.root).build()
        installAddons = project.tasks.create('installAddons', InstallAddons)
        installAddons.installAddonsService = new InstallAddonsService(herokuClient)
        installAddons.apiKey = API_KEY

        def app = new HerokuApp(project)
        app.name = APP_NAME
        def redisAddon = new HerokuAddon('redis')
        redisAddon.plan = PLAN
        redisAddon.waitUntilStarted = true
        app.addons = [redisAddon]

        installAddons.bundle = [app]
    }

    def "install an addon when missing"() {
        given:
        herokuClient.getAddonAttachments(APP_NAME) >> []
        herokuClient.listConfig(APP_NAME) >> ['REDIS_URL': "http://127.0.0.1:${serverSocket.localPort}"]

        when:
        installAddons.installAddons()

        then:
        1 * herokuClient.installAddon(APP_NAME, PLAN)
    }

    def "skip installing an addon when already exists"() {
        given:
        herokuClient.getAddonAttachments(APP_NAME) >> [['name': 'REDIS']]

        when:
        installAddons.installAddons()

        then:
        0 * herokuClient.installAddon(APP_NAME, PLAN)
    }
}
