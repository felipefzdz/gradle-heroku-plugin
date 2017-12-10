package com.felipefzdz.gradle.heroku.tasks

import com.felipefzdz.gradle.heroku.tasks.model.HerokuApp
import com.felipefzdz.gradle.heroku.tasks.services.InstallAddonsService
import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

@CompileStatic
class InstallAddonsTask extends DefaultTask {

    @Internal
    Property<String> apiKey

    @Internal
    HerokuApp app

    @Internal
    InstallAddonsService installAddonsService


    @TaskAction
    void installAddons() {
        installAddonsService.installAddons(app.addons.toList(), apiKey.get(), app.name)
    }
}
