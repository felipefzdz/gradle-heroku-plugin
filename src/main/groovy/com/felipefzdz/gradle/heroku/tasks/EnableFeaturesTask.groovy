package com.felipefzdz.gradle.heroku.tasks

import com.felipefzdz.gradle.heroku.tasks.model.HerokuApp
import com.felipefzdz.gradle.heroku.tasks.services.EnableFeaturesService
import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

@CompileStatic
class EnableFeaturesTask extends DefaultTask {
    @Internal
    Property<String> apiKey

    @Internal
    HerokuApp app

    @Internal
    EnableFeaturesService enableFeaturesService

    @TaskAction
    void enableFeatures() {
        enableFeaturesService.enableFeatures(app.features, apiKey.get(), app.name)
    }
}
