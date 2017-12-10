package com.felipefzdz.gradle.heroku

import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.DeployBundleTask
import com.felipefzdz.gradle.heroku.tasks.model.HerokuAddon
import com.felipefzdz.gradle.heroku.tasks.model.HerokuApp
import com.felipefzdz.gradle.heroku.tasks.model.HerokuWebApp
import com.felipefzdz.gradle.heroku.tasks.services.AddAddonAttachmentsService
import com.felipefzdz.gradle.heroku.tasks.services.ConfigureLogDrainsService
import com.felipefzdz.gradle.heroku.tasks.services.CreateBuildService
import com.felipefzdz.gradle.heroku.tasks.services.DeployService
import com.felipefzdz.gradle.heroku.tasks.services.EnableFeaturesService
import com.felipefzdz.gradle.heroku.tasks.services.InstallAddonsService
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.internal.DefaultDomainObjectCollection
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.Subject

class DeployBundleIntegTest extends Specification {

    @Rule
    TemporaryFolder temporaryFolder = new TemporaryFolder()

    HerokuClient herokuClient = Mock(HerokuClient)

    @Subject
    DeployBundleTask deploy

    String API_KEY = 'apiKey'
    String APP_NAME = 'appName'
    String TEAM_NAME = 'teamName'
    String STACK = 'cedar-14'
    Boolean PERSONAL_APP = true
    String PLAN = 'heroku-redis:hobby-dev'

    def setup() {
        def project = ProjectBuilder.builder().withProjectDir(temporaryFolder.root).build()
        deploy = project.tasks.create('deployBundleTask', DeployBundleTask)

        deploy.herokuClient = herokuClient
        deploy.deployService = new DeployService(new InstallAddonsService(herokuClient), herokuClient,
                new ConfigureLogDrainsService(herokuClient), new CreateBuildService(herokuClient),
                new EnableFeaturesService(herokuClient), new AddAddonAttachmentsService(herokuClient))

        def apiKeyProperty = project.objects.property(String)
        apiKeyProperty.set(API_KEY)
        deploy.apiKey = apiKeyProperty

        def app = new HerokuWebApp(APP_NAME, null, null)
        app.teamName = TEAM_NAME
        app.personalApp = PERSONAL_APP
        app.stack = STACK
        app.recreate = false

        deploy.bundle = new DefaultDomainObjectCollection(HerokuApp, [app]) as HerokuAppContainer

        herokuClient.getAddonAttachments(APP_NAME) >> []
    }

    def "create an app when missing"() {
        given:
        herokuClient.appExists(APP_NAME) >> false

        when:
        deploy.deployBundle()

        then:
        1 * herokuClient.createApp(APP_NAME, TEAM_NAME, PERSONAL_APP, STACK)
    }

    def "skip creating an app when already exists"() {
        given:
        herokuClient.appExists(APP_NAME) >> true

        when:
        deploy.deployBundle()

        then:
        0 * herokuClient.createApp(APP_NAME, TEAM_NAME, PERSONAL_APP, STACK)
    }

    def "destroy and create an app when recreate"() {
        given:
        deploy.bundle[0].recreate = true
        deploy.delayAfterDestroyApp = 0
        herokuClient.appExists(APP_NAME) >> true

        when:
        deploy.deployBundle()

        then:
        1 * herokuClient.destroyApp(APP_NAME)
        1 * herokuClient.createApp(APP_NAME, TEAM_NAME, PERSONAL_APP, STACK)
    }

    def "skip destroying an app when missing"() {
        given:
        deploy.bundle[0].recreate = true
        herokuClient.appExists(APP_NAME) >> false

        when:
        deploy.deployBundle()

        then:
        0 * herokuClient.destroyApp(APP_NAME)
    }
}
