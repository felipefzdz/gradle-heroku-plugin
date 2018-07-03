package com.felipefzdz.gradle.heroku.tasks

import com.felipefzdz.gradle.heroku.tasks.model.HerokuWebApp
import com.felipefzdz.gradle.heroku.tasks.services.EnableFeaturesService
import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

import javax.inject.Inject

@CompileStatic
class EnableFeaturesTask extends HerokuBaseTask {
    @Internal
    HerokuWebApp app

    private final EnableFeaturesService enableFeaturesService

    @Inject
    EnableFeaturesTask(EnableFeaturesService enableFeaturesService) {
        this.enableFeaturesService = enableFeaturesService
    }

    @TaskAction
    void enableFeatures() {
        enableFeaturesService.enableFeatures(app.features, app.name)
    }
}
