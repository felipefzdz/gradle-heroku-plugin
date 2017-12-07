package com.felipefzdz.gradle.heroku.tasks

import com.felipefzdz.gradle.heroku.heroku.DefaultHerokuClient
import com.felipefzdz.gradle.heroku.tasks.model.HerokuApp
import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

@CompileStatic
class InstallAddons extends DefaultTask {

    @Internal
    Property<String> apiKey

    @Internal
    Collection<HerokuApp> bundle

    @Internal
    InstallAddonsService installAddonsService

    InstallAddons() {
        this.apiKey = project.objects.property(String)
        this.bundle = project.objects.listProperty(HerokuApp) as List<HerokuApp>
        this.installAddonsService = new InstallAddonsService(new DefaultHerokuClient())
        outputs.upToDateWhen { false }
    }

    @TaskAction
    def installAddons() {
        bundle.each { HerokuApp app ->
            installAddonsService.installAddons(app.addons.toList(), apiKey.get(), app.name)
        }
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

    void setBundle(Collection<HerokuApp> bundle) {
        this.bundle = bundle
    }
}

