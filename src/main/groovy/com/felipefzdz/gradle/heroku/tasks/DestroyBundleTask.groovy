package com.felipefzdz.gradle.heroku.tasks

import com.felipefzdz.gradle.heroku.HerokuAppContainer
import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.model.HerokuApp
import com.felipefzdz.gradle.heroku.tasks.services.DestroyAppService
import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

import javax.inject.Inject

@CompileStatic
class DestroyBundleTask extends HerokuBaseTask {

    @Internal
    HerokuAppContainer bundle

    private final HerokuClient herokuClient
    private final DestroyAppService destroyAppService

    @Inject
    DestroyBundleTask(HerokuClient herokuClient, DestroyAppService destroyAppService) {
        this.herokuClient = herokuClient
        this.destroyAppService = destroyAppService
    }

    @TaskAction
    void destroyBundle() {
        bundle.toList().each { HerokuApp app ->
            destroyAppService.destroyApp(app.name)
        }
    }
}
