package com.felipefzdz.gradle.heroku

import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.Deploy
import com.felipefzdz.gradle.heroku.tasks.InstallAddons
import com.felipefzdz.gradle.heroku.tasks.InstallAddonsService
import com.felipefzdz.gradle.heroku.tasks.model.HerokuAddon
import com.heroku.api.Addon
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.Subject

class InstallAddonsIntegTest extends Specification {

    @Rule
    TemporaryFolder temporaryFolder = new TemporaryFolder()

    HerokuClient herokuClient = Mock(HerokuClient)

    @Subject
    InstallAddons installAddons

    String API_KEY = 'apiKey'
    String APP_NAME = 'appName'
    String PLAN = 'heroku-redis:hobby-dev'

    def setup() {
        def project = ProjectBuilder.builder().withProjectDir(temporaryFolder.root).build()
        installAddons = project.tasks.create('installAddons', InstallAddons)
        installAddons.installAddonsService = new InstallAddonsService(herokuClient)
        installAddons.apiKey = API_KEY
        installAddons.appName = APP_NAME
        def redisAddon = new HerokuAddon('redis')
        redisAddon.plan = PLAN
        installAddons.addons = [redisAddon]
    }

    def "install an addon when missing"() {
        given:
        herokuClient.getAddonAttachments(APP_NAME) >> []

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
