package com.felipefzdz.gradle.heroku.tasks

import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.model.HerokuWebApp
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
    HerokuWebApp app

    @Internal
    HerokuClient herokuClient

    @Internal
    InstallAddonsService installAddonsService


    @TaskAction
    void installAddons() {
        herokuClient.init(apiKey.get())
        installAddonsService.installAddons(app.addons.toList(), apiKey.get(), app.name)
    }
}
