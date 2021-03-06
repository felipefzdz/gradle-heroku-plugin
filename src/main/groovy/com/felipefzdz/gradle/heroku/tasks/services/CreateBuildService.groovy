package com.felipefzdz.gradle.heroku.tasks.services

import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.model.BuildSource
import groovy.transform.CompileStatic
import org.gradle.api.logging.Logger

import java.time.Duration

import static com.felipefzdz.gradle.heroku.utils.AsyncUtil.waitFor

@CompileStatic
class CreateBuildService {

    private final Boolean SKIP_WAITS = Boolean.valueOf(System.getenv('GRADLE_HEROKU_PLUGIN_SKIP_WAITS'))

    private final HerokuClient herokuClient
    private final Logger logger

    CreateBuildService(HerokuClient herokuClient, Logger logger) {
        this.herokuClient = herokuClient
        this.logger = logger
    }

    void createBuild(BuildSource build, String appName) {
        if (build) {
            updateBuildpack(appName, build.buildpackUrl)
            logger.lifecycle "Deploying artifact ${build.buildUrl.get()} to application $appName"
            String buildId = herokuClient.createBuild(appName, build.buildVersion, build.buildUrl.get()).id
            verifyHerokuBuild(appName, buildId)
        }
    }

    private void updateBuildpack(String appName, String buildpackUrl) {
        logger.lifecycle "Setting buildpack to $buildpackUrl"
        herokuClient.setBuildPack(appName, buildpackUrl)
    }

    private void verifyHerokuBuild(String appName, String buildId) {
        logger.lifecycle "Verifying heroku build succeeded for app: $appName and buildId: $buildId"
        def status = waitFor(Duration.ofMinutes(5), Duration.ofSeconds(5), "Waiting for heroku build status for: $appName", SKIP_WAITS) {
            def props = herokuClient.getBuildRequest(appName, buildId)
            logger.lifecycle "Current heroku build status is: '${props.status}'"
            assert props.status != 'pending'
            props.status as String
        }
        if (status == 'failed') {
            throw new RuntimeException("Failed to create a build for $appName. Visit the heroku dashboard to diagnose (Activity > View build log)")
        } else {
            logger.lifecycle "Created build for $appName"
        }
    }

}
