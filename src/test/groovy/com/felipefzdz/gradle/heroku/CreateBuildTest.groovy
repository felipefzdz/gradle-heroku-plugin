package com.felipefzdz.gradle.heroku

import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.model.BuildSource
import com.felipefzdz.gradle.heroku.tasks.services.CreateBuildService
import spock.lang.Specification

class CreateBuildTest extends Specification {

    CreateBuildService createBuildService
    HerokuClient herokuClient = Mock(HerokuClient)

    String APP_NAME = 'appName'
    String BUILDPACK_URL = 'buildpackUrl'
    String BUILD_VERSION = 'buildVersion'
    String BUILD_URL = 'buildUrl'
    BuildSource buildSource

    def setup() {
        createBuildService = new CreateBuildService(herokuClient)

        buildSource = new BuildSource()
        buildSource.buildUrl = { BUILD_URL }
        buildSource.buildVersion = BUILD_VERSION
        buildSource.buildpackUrl = BUILDPACK_URL
    }

    def "create a build"() {
        when:
        createBuildService.createBuild(buildSource, APP_NAME)

        then:
        1 * herokuClient.setBuildPack(APP_NAME, BUILDPACK_URL)
        1 * herokuClient.createBuild(APP_NAME, BUILD_VERSION, BUILD_URL) >> ['id': '123']
        1 * herokuClient.getBuildRequest(APP_NAME, _) >> ['status': 'succeeded']
    }

    def "fail when Heroku returns an error"() {
        when:
        createBuildService.createBuild(buildSource, APP_NAME)

        then:
        1 * herokuClient.setBuildPack(APP_NAME, BUILDPACK_URL)
        1 * herokuClient.createBuild(APP_NAME, BUILD_VERSION, BUILD_URL) >> ['id': '123']
        1 * herokuClient.getBuildRequest(APP_NAME, _) >> ['status': 'failed']

        and:
        def e = thrown(RuntimeException)
        e.message == "Failed to create a build for $APP_NAME. Visit the heroku dashboard to diagnose (Activity > View build log)"
    }

}
