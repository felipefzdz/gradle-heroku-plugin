package com.felipefzdz.gradle.heroku.tasks

import com.felipefzdz.gradle.heroku.HerokuAppContainer
import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.model.HerokuApp
import com.felipefzdz.gradle.heroku.tasks.services.ConfigureLogDrainsService
import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

@CompileStatic
class ConfigureLogDrainsForBundleTask extends DefaultTask {
    @Internal
    Property<String> apiKey

    @Internal
    HerokuAppContainer bundle

    @Internal
    HerokuClient herokuClient

    @Internal
    ConfigureLogDrainsService configureLogDrainsService

    @TaskAction
    void configureLogDrains() {
        herokuClient.init(apiKey.get())
        bundle.all { HerokuApp app ->
            configureLogDrainsService.configureLogDrains(app.logDrains, apiKey.get(), app.name)
        }
    }
}
