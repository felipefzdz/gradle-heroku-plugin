package com.felipefzdz.gradle.heroku

import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.DestroyBundleTask
import com.felipefzdz.gradle.heroku.tasks.model.HerokuApp
import org.gradle.api.internal.DefaultDomainObjectCollection
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.Subject

class DestroyBundleIntegTest extends Specification {

    @Rule
    TemporaryFolder temporaryFolder = new TemporaryFolder()

    HerokuClient herokuClient = Mock(HerokuClient)

    @Subject
    DestroyBundleTask destroyBundleTask

    String API_KEY = 'apiKey'
    String APP_NAME = 'appName'

    def setup() {
        def project = ProjectBuilder.builder().withProjectDir(temporaryFolder.root).build()
        destroyBundleTask = project.tasks.create('destroyBundle', DestroyBundleTask)
        destroyBundleTask.herokuClient = herokuClient
        def apiKeyProperty = project.objects.property(String)
        apiKeyProperty.set(API_KEY)
        destroyBundleTask.apiKey = apiKeyProperty

        def app = new HerokuApp(APP_NAME)
        destroyBundleTask.bundle = new DefaultDomainObjectCollection(HerokuApp, [app]) as HerokuAppContainer
    }

    def "skip destroying an app when missing"() {
        given:
        herokuClient.appExists(APP_NAME) >> false

        when:
        destroyBundleTask.destroyBundle()

        then:
        0 * herokuClient.destroyApp(APP_NAME)
    }

    def "destroy an app when already exists"() {
        given:
        herokuClient.appExists(APP_NAME) >> true

        when:
        destroyBundleTask.destroyBundle()

        then:
        1 * herokuClient.destroyApp(APP_NAME)
    }

}
