package com.felipefzdz.gradle.heroku.tasks

import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.model.HerokuApp
import com.felipefzdz.gradle.heroku.tasks.services.ConfigureLogDrainsService
import groovy.transform.CompileStatic
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

import javax.inject.Inject

@CompileStatic
class ConfigureLogDrainsTask extends HerokuBaseTask {

    private static final int ADD_LOG_DRAINS_RETRIES = 3

    @Internal
    HerokuApp app

    private final HerokuClient herokuClient
    private final ConfigureLogDrainsService configureLogDrainsService

    @Inject
    ConfigureLogDrainsTask(HerokuClient herokuClient, ConfigureLogDrainsService configureLogDrainsService) {
        this.herokuClient = herokuClient
        this.configureLogDrainsService = configureLogDrainsService
    }

    @TaskAction
    void configureLogDrains() {
        configureLogDrainsService.configureLogDrains(app.logDrains, app.name, ADD_LOG_DRAINS_RETRIES)
    }
}
