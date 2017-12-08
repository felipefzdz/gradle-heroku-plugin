package com.felipefzdz.gradle.heroku

import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.InstallAddonsForBundleTask
import com.felipefzdz.gradle.heroku.tasks.model.HerokuAddon
import com.felipefzdz.gradle.heroku.tasks.model.HerokuApp
import com.felipefzdz.gradle.heroku.tasks.model.HerokuWebApp
import com.felipefzdz.gradle.heroku.tasks.services.InstallAddonsService
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.internal.DefaultDomainObjectCollection
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

class InstallAddonsForBundleIntegTest extends Specification {

    @Rule
    TemporaryFolder temporaryFolder = new TemporaryFolder()

    @Subject
    InstallAddonsForBundleTask installAddons

    @Shared
    @AutoCleanup
    ServerSocket serverSocket = new ServerSocket(0)

    HerokuClient herokuClient = Mock(HerokuClient)

    String API_KEY = 'apiKey'
    String APP_NAME = 'appName'
    String PLAN = 'heroku-redis:hobby-dev'

    def setup() {
        def project = ProjectBuilder.builder().withProjectDir(temporaryFolder.root).build()
        installAddons = project.tasks.create('installAddonsForBundleTask', InstallAddonsForBundleTask)
        installAddons.herokuClient = herokuClient
        installAddons.installAddonsService = new InstallAddonsService(herokuClient)
        def apiKeyProperty = project.objects.property(String)
        apiKeyProperty.set(API_KEY)
        installAddons.apiKey = apiKeyProperty

        def redisAddon = new HerokuAddon('redis')
        redisAddon.plan = PLAN
        redisAddon.waitUntilStarted = true
        def addons = new DefaultDomainObjectCollection(HerokuAddon, [redisAddon]) as NamedDomainObjectContainer<HerokuAddon>

        def app = new HerokuWebApp(APP_NAME, addons)

        installAddons.bundle = new DefaultDomainObjectCollection(HerokuApp, [app]) as HerokuAppContainer
    }

    def "install an addon when missing"() {
        given:
        herokuClient.getAddonAttachments(APP_NAME) >> []
        herokuClient.listConfig(APP_NAME) >> ['REDIS_URL': "http://127.0.0.1:${serverSocket.localPort}"]

        when:
        installAddons.installAddonsForBundle()

        then:
        1 * herokuClient.installAddon(APP_NAME, PLAN)
    }

    def "skip installing an addon when already exists"() {
        given:
        herokuClient.getAddonAttachments(APP_NAME) >> [['name': 'REDIS']]

        when:
        installAddons.installAddonsForBundle()

        then:
        0 * herokuClient.installAddon(APP_NAME, PLAN)
    }
}
