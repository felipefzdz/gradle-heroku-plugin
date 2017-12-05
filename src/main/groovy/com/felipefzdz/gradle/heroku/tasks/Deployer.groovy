package com.felipefzdz.gradle.heroku.tasks

import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.TaskAction

import java.time.Duration

import static com.felipefzdz.gradle.heroku.heroku.HerokuClientFactory.create

@CompileStatic
class Deployer extends DefaultTask {

    Property<String> apiKey
    Property<String> appName
    Property<String> teamName
    Property<Boolean> personalApp
    HerokuClient herokuClient

    Deployer() {
        outputs.upToDateWhen { false }
    }

    @TaskAction
    def herokuDeploy() {
        herokuClient = create(logger, apiKey.get())
        boolean recreate = false
        maybeCreateApplication(appName.get(), teamName.get(), recreate)
        logger.quiet("Successfully deployed app ${appName.get()}")
    }

    def maybeCreateApplication(String appName, String teamName, boolean recreate) {
        boolean exists = herokuClient.appExists(appName)
        if (exists && recreate) {
            herokuClient.destroyApp(appName)
            delay(Duration.ofSeconds(20))
            exists = false
        }
        if (!exists) {
            herokuClient.createOrganization(appName, teamName, personalApp.get())
        }
    }


    private delay(Duration duration) {
        logger.quiet("Delaying for ${duration.toMillis()} milliseconds...")
        sleep(duration.toMillis())
    }
}
