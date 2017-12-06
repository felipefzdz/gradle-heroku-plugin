package com.felipefzdz.gradle.heroku.tasks

import com.felipefzdz.gradle.heroku.heroku.DefaultHerokuClient
import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.model.HerokuAddon
import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

@CompileStatic
class InstallAddons extends DefaultTask {

    @Internal
    Property<String> apiKey

    @Internal
    Property<String> appName

    @Internal
    NamedDomainObjectContainer<HerokuAddon> addons

    @Internal
    InstallAddonsService installAddonsService

    InstallAddons() {
        this.apiKey = project.objects.property(String)
        this.appName = project.objects.property(String)
        this.addons = project.container(HerokuAddon)
        outputs.upToDateWhen { false }
        this.installAddonsService = new InstallAddonsService(new DefaultHerokuClient())
    }

    @TaskAction
    def installAddons() {
        installAddonsService.installAddons(addons.toList(), apiKey.get(), appName.get())
    }

    void setInstallAddonsService(InstallAddonsService installAddonsService) {
        this.installAddonsService = installAddonsService
    }

    void setApiKey(Property<String> apiKey) {
        this.apiKey = apiKey
    }

    void setApiKey(String apiKey) {
        this.apiKey.set(apiKey)
    }

    void setAppName(Property<String> appName) {
        this.appName = appName
    }

    void setAppName(String appName) {
        this.appName.set(appName)
    }

    void setAddons(NamedDomainObjectContainer<HerokuAddon> addons) {
        this.addons = addons
    }

    void setAddons(List<HerokuAddon> addons) {
        addons.each { HerokuAddon addon ->
            this.addons.create(addon.name, { HerokuAddon it ->
                it.plan = addon.plan
            })
        }
    }
}

