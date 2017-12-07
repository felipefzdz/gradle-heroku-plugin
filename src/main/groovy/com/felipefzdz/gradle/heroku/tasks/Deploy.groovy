package com.felipefzdz.gradle.heroku.tasks

import com.felipefzdz.gradle.heroku.heroku.DefaultHerokuClient
import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.model.HerokuAddon
import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

import java.time.Duration

@CompileStatic
class Deploy extends DefaultTask {

    @Internal
    Property<String> apiKey

    @Internal
    Property<String> appName

    @Internal
    @Optional
    Property<String> teamName

    @Internal
    @Optional
    Property<Boolean> personalApp

    @Internal
    @Optional
    Property<String> stack

    @Internal
    @Optional
    Property<Boolean> recreate

    @Internal
    NamedDomainObjectContainer<HerokuAddon> addons

    @Internal
    HerokuClient herokuClient

    @Internal
    InstallAddonsService installAddonsService

    @Internal
    int delayAfterDestroyApp = 20

    Deploy() {
        this.apiKey = project.objects.property(String)
        this.appName = project.objects.property(String)
        this.teamName = project.objects.property(String)
        this.personalApp = project.objects.property(Boolean)
        this.stack = project.objects.property(String)
        this.recreate = project.objects.property(Boolean)
        this.addons = project.container(HerokuAddon)
        this.herokuClient = new DefaultHerokuClient()
        this.installAddonsService = new InstallAddonsService(this.herokuClient)
        outputs.upToDateWhen { false }
    }

    @TaskAction
    def deploy() {
        herokuClient.init(apiKey.get())
        maybeCreateApplication(appName.get(), teamName.getOrElse(''), recreate.get(), stack.getOrElse('heroku-16'))
        installAddons()
        println "Successfully deployed app ${appName.get()}"
    }

    void maybeCreateApplication(String appName, String teamName, boolean recreate, String stack) {
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
            herokuClient.createApp(appName, teamName, personalApp.get(), stack)
            println "Successfully created app $appName"
        }
    }

    void installAddons() {
       installAddonsService.installAddons(addons.toList(), apiKey.get(), appName.get())
    }

    private delay(Duration duration) {
        println "Delaying for ${duration.toMillis()} milliseconds..."
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

    void setStack(String stack) {
        this.stack.set(stack)
    }

    void setPersonalApp(Boolean personalApp) {
        this.personalApp.set(personalApp)
    }

    void setRecreate(Boolean recreate) {
        this.recreate.set(recreate)
    }

    void setApiKey(Property<String> apiKey) {
        this.apiKey = apiKey
    }

    void setAppName(Property<String> appName) {
        this.appName = appName
    }

    void setTeamName(Property<String> teamName) {
        this.teamName = teamName
    }

    void setPersonalApp(Property<Boolean> personalApp) {
        this.personalApp = personalApp
    }

    void setStack(Property<String> stack) {
        this.stack = stack
    }

    void setRecreate(Property<Boolean> recreate) {
        this.recreate = recreate
    }

    void setHerokuClient(HerokuClient herokuClient) {
        this.herokuClient = herokuClient
    }

    void setAddons(NamedDomainObjectContainer<HerokuAddon> addons) {
        this.addons = addons
    }

    void setAddons(List<HerokuAddon> addons) {
        addons.each { HerokuAddon addon ->
            this.addons.create(addon.name, { HerokuAddon it ->
                it.plan = addon.plan
                it.waitUntilStarted = addon.waitUntilStarted
            })
        }
    }
}
