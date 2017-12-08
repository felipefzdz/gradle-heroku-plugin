package com.felipefzdz.gradle.heroku

import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.ConfigureLogDrainsForBundleTask
import com.felipefzdz.gradle.heroku.tasks.model.HerokuApp
import com.felipefzdz.gradle.heroku.tasks.services.ConfigureLogDrainsService
import org.gradle.api.internal.DefaultDomainObjectCollection
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.Subject

class ConfigureLogDrainsForBundleIntegTest extends Specification {

    @Rule
    TemporaryFolder temporaryFolder = new TemporaryFolder()

    @Subject
    ConfigureLogDrainsForBundleTask configureTask

    HerokuClient herokuClient = Mock(HerokuClient)

    String API_KEY = 'apiKey'
    String APP_NAME = 'appName'

    def setup() {
        def project = ProjectBuilder.builder().withProjectDir(temporaryFolder.root).build()
        configureTask = project.tasks.create('configureLogDrainsForBundleTask', ConfigureLogDrainsForBundleTask)
        configureTask.herokuClient = herokuClient
        configureTask.configureLogDrainsService = new ConfigureLogDrainsService(herokuClient)

        def apiKeyProperty = project.objects.property(String)
        apiKeyProperty.set(API_KEY)
        configureTask.apiKey = apiKeyProperty

        def app = new HerokuApp(APP_NAME)
        app.logDrains(['test1', 'test2'])

        configureTask.bundle = new DefaultDomainObjectCollection(HerokuApp, [app]) as HerokuAppContainer
    }

    def "add log drains only when missing"() {
        given:
        herokuClient.listLogDrains(APP_NAME) >> [['url': 'test1']]

        when:
        configureTask.configureLogDrains()

        then:
        0 * herokuClient.addLogDrain(APP_NAME, 'test1')
        1 * herokuClient.addLogDrain(APP_NAME, 'test2')
    }

}
