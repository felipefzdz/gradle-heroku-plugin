package com.felipefzdz.gradle.heroku.tasks

import com.felipefzdz.gradle.heroku.heroku.api.OrganizationAppCreateRequest
import com.heroku.api.Heroku
import com.heroku.api.HerokuAPI
import com.heroku.api.request.Request
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

import java.time.Duration

class Deployer extends DefaultTask {

    @Input
    Property<String> apiKey

    @Input
    Property<String> appName

    @Input
    Property<String> teamName

    @Input
    Property<Boolean> personalApp

    HerokuAPI herokuApi

    Deployer() {
        this.apiKey = project.objects.property(String.class);
        this.appName = project.objects.property(String.class);
        this.teamName = project.objects.property(String.class);
        this.personalApp = project.objects.property(Boolean.class);
    }

    @TaskAction
    def herokuDeploy() {
        herokuApi = new HerokuAPI(apiKey.get())
        boolean recreate = false
        maybeCreateApplication(appName.get(), teamName.get(), recreate)
        logger.quiet("Successfully deployed app ${appName.get()}")
    }

    def maybeCreateApplication(String appName, String teamName, boolean recreate) {
        boolean exists = herokuApi.appExists(appName)
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
        herokuApi.destroyApp(appName)
        delay(Duration.ofSeconds(20))
    }

    private <T> T api3(Request<T> request) {
        herokuApi.connection.execute(request, apiKey.get())
    }

    private delay(Duration duration) {
        logger.quiet("Delaying for ${duration.toMillis()} milliseconds...")
        sleep(duration.toMillis())
    }
}
