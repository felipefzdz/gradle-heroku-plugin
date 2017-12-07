package com.felipefzdz.gradle.heroku.tasks

import com.felipefzdz.gradle.heroku.heroku.DefaultHerokuClient
import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.model.HerokuAddon
import com.felipefzdz.gradle.heroku.tasks.model.HerokuApp
import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

import java.time.Duration

@CompileStatic
class Deploy extends DefaultTask {

    @Internal
    Property<String> apiKey

    @Internal
    Collection<HerokuApp> apps

    @Internal
    HerokuClient herokuClient

    @Internal
    InstallAddonsService installAddonsService

    @Internal
    int delayAfterDestroyApp = 20

    Deploy() {
        this.apiKey = project.objects.property(String)
        this.apps = project.objects.listProperty(HerokuApp) as List<HerokuApp>
        this.herokuClient = new DefaultHerokuClient()
        this.installAddonsService = new InstallAddonsService(this.herokuClient)
        outputs.upToDateWhen { false }
    }

    @TaskAction
    def deploy() {
        herokuClient.init(apiKey.get())
        apps.each { HerokuApp app ->
            maybeCreateApplication(app.name, app.teamName, app.recreate, app.stack, app.personalApp)
            installAddons(app.addons.toList(), app.name)
            println "Successfully deployed app ${app.name}"
        }
    }

    void maybeCreateApplication(String appName, String teamName, boolean recreate, String stack, boolean personalApp) {
        boolean exists = herokuClient.appExists(appName)
        if (exists && recreate) {
            herokuClient.destroyApp(appName)
            // Give some time to Heroku to actually destroy the app
            delay(Duration.ofSeconds(delayAfterDestroyApp))
            exists = false
        }
        if (exists) {
            println "App $appName already exists and won't be created"
        } else {
            herokuClient.createApp(appName, teamName, personalApp, stack)
            println "Successfully created app $appName"
        }
    }

    void installAddons(List<HerokuAddon> addons, String appName) {
       installAddonsService.installAddons(addons, apiKey.get(), appName)
    }

    private delay(Duration duration) {
        println "Delaying for ${duration.toMillis()} milliseconds..."
        sleep(duration.toMillis())
    }

    void setApiKey(String apiKey) {
        this.apiKey.set(apiKey)
    }

    void setApiKey(Property<String> apiKey) {
        this.apiKey = apiKey
    }

    void setApps(Collection<HerokuApp> apps) {
        this.apps = apps
    }

    void setHerokuClient(HerokuClient herokuClient) {
        this.herokuClient = herokuClient
    }
}
