package com.felipefzdz.gradle.heroku.tasks

import com.felipefzdz.gradle.heroku.HerokuAppContainer
import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.model.HerokuApp
import com.felipefzdz.gradle.heroku.tasks.services.DestroyAppService
import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

@CompileStatic
class DestroyBundleTask extends HerokuBaseTask {

    @Internal
    HerokuAppContainer bundle

    @Internal
    HerokuClient herokuClient

    @Internal
    DestroyAppService destroyAppService

    @TaskAction
    void destroyBundle() {
        bundle.toList().each { HerokuApp app ->
            destroyAppService.destroyApp(app.name)
        }
    }
}
