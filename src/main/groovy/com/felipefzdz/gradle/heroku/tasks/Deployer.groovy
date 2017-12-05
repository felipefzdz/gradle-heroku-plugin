package com.felipefzdz.gradle.heroku.tasks

import com.felipefzdz.gradle.heroku.heroku.DefaultHerokuClient
import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.TaskAction

import java.time.Duration

@CompileStatic
class Deployer extends DefaultTask {

    Property<String> apiKey
    Property<String> appName
    Property<String> teamName
    Property<Boolean> personalApp
    Property<Boolean> recreate

    HerokuClient herokuClient
    int delayAfterDestroyApp = 20

    Deployer() {
        this.apiKey = project.objects.property(String)
        this.appName = project.objects.property(String)
        this.teamName = project.objects.property(String)
        this.personalApp = project.objects.property(Boolean)
        this.recreate = project.objects.property(Boolean)
        this.herokuClient = new DefaultHerokuClient(logger)
        outputs.upToDateWhen { false }
    }

    @TaskAction
    def deploy() {
        herokuClient.init(apiKey.get())
        maybeCreateApplication(appName.get(), teamName.get(), recreate.get())
        logger.quiet("Successfully deployed app ${appName.get()}")
    }

    def maybeCreateApplication(String appName, String teamName, boolean recreate) {
        boolean exists = herokuClient.appExists(appName)
        if (exists && recreate) {
            herokuClient.destroyApp(appName)
            // Give some time to Heroku to actually destroy the app
            delay(Duration.ofSeconds(delayAfterDestroyApp))
            exists = false
        }
        if (!exists) {
            herokuClient.createApp(appName, teamName, personalApp.get())
        }
    }

    private delay(Duration duration) {
        logger.quiet("Delaying for ${duration.toMillis()} milliseconds...")
        sleep(duration.toMillis())
    }

    void setApiKey(String apiKey) {
        this.apiKey.set(apiKey)
    }

    void setAppName(String appName) {
        this.appName.set(appName)
    }

    void setTeamName(String teamName) {
        this.teamName.set(teamName)
    }

    void setPersonalApp(Boolean personalApp) {
        this.personalApp.set(personalApp)
    }

    void setRecreate(Boolean recreate) {
        this.recreate.set(recreate)
    }

    void setHerokuClient(HerokuClient herokuClient) {
        this.herokuClient = herokuClient
    }
}
