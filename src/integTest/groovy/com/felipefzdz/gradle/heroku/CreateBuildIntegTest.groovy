package com.felipefzdz.gradle.heroku

import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.CreateBuildTask
import com.felipefzdz.gradle.heroku.tasks.model.BuildSource
import com.felipefzdz.gradle.heroku.tasks.model.HerokuApp
import com.felipefzdz.gradle.heroku.tasks.services.CreateBuildService
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.Subject

class CreateBuildIntegTest extends Specification {

    @Rule
    TemporaryFolder temporaryFolder = new TemporaryFolder()

    @Subject
    CreateBuildTask createBuild

    HerokuClient herokuClient = Mock(HerokuClient)

    String API_KEY = 'apiKey'
    String APP_NAME = 'appName'
    String BUILDPACK_URL = 'buildpackUrl'
    String BUILD_VERSION = 'buildVersion'
    String BUILD_URL = 'buildUrl'

    def setup() {
        def project = ProjectBuilder.builder().withProjectDir(temporaryFolder.root).build()
        createBuild = project.tasks.create('createBuildTask', CreateBuildTask)
        createBuild.herokuClient = herokuClient
        createBuild.createBuildService = new CreateBuildService(herokuClient)

        def apiKeyProperty = project.objects.property(String)
        apiKeyProperty.set(API_KEY)
        createBuild.apiKey = apiKeyProperty

        def buildSource = new BuildSource()
        buildSource.buildUrl = BUILD_URL
        buildSource.buildVersion = BUILD_VERSION
        buildSource.buildpackUrl = BUILDPACK_URL

        def app = new HerokuApp(APP_NAME)
        app.buildSource = buildSource

        createBuild.app = app
    }

    def "create a build"() {
        when:
        createBuild.createBuild()

        then:
        1 * herokuClient.setBuildPack(APP_NAME, BUILDPACK_URL)
        1 * herokuClient.createBuild(APP_NAME, BUILD_VERSION, BUILD_URL) >> ['id': '123']
        1 * herokuClient.getBuildRequest(APP_NAME, _) >> ['status': 'succeeded']
    }

    def "fail when Heroku returns an error"() {
       when:
       createBuild.createBuild()

        then:
        1 * herokuClient.setBuildPack(APP_NAME, BUILDPACK_URL)
        1 * herokuClient.createBuild(APP_NAME, BUILD_VERSION, BUILD_URL) >> ['id': '123']
        1 * herokuClient.getBuildRequest(APP_NAME, _) >> ['status': 'failed']

        and:
        def e = thrown(RuntimeException)
        e.message == "Failed to create a build for $APP_NAME. Visit the heroku dashboard to diagnose (Activity > View build log)"
    }

}
