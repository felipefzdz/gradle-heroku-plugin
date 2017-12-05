package com.felipefzdz.gradle.heroku.tasks

import com.felipefzdz.gradle.heroku.heroku.api.OrganizationAppCreateRequest
import com.heroku.api.Heroku
import com.heroku.api.HerokuAPI
import com.heroku.api.request.Request
import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.TaskAction

import java.time.Duration

import static com.felipefzdz.gradle.heroku.heroku.HerokuAPIFactory.create

@CompileStatic
class Deployer extends DefaultTask {

    Property<String> apiKey
    Property<String> appName
    Property<String> teamName
    Property<Boolean> personalApp
    HerokuAPI herokuAPI

    Deployer() {
        outputs.upToDateWhen { false }
    }

    @TaskAction
    def herokuDeploy() {
        herokuAPI = create(apiKey.get())
        boolean recreate = false
        maybeCreateApplication(appName.get(), teamName.get(), recreate)
        logger.quiet("Successfully deployed app ${appName.get()}")
    }

    def maybeCreateApplication(String appName, String teamName, boolean recreate) {
        boolean exists = herokuAPI.appExists(appName)
        if (exists && recreate) {
            logger.quiet("Destroying existing heroku app $appName")
            destroyApp(appName)
            exists = false
        }
        if (!exists) {
            logger.quiet("Creating heroku app $appName for team $teamName")
            api3(new OrganizationAppCreateRequest(appName, teamName, Heroku.Stack.Cedar14, personalApp.get()))
        }
    }

    def destroyApp(String appName) {
        logger.quiet("Destroying application $appName")
        herokuAPI.destroyApp(appName)
        delay(Duration.ofSeconds(20))
    }

    private <T> T api3(Request<T> request) {
        herokuAPI.connection.execute(request, apiKey.get())
    }

    private delay(Duration duration) {
        logger.quiet("Delaying for ${duration.toMillis()} milliseconds...")
        sleep(duration.toMillis())
    }
}
