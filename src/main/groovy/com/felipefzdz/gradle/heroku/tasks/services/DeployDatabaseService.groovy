package com.felipefzdz.gradle.heroku.tasks.services

import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.model.HerokuDatabaseApp
import groovy.transform.CompileStatic
import org.gradle.api.logging.Logger

@CompileStatic
class DeployDatabaseService extends BaseDeployService {

    private static final int ADD_LOG_DRAINS_RETRIES = 3

    DeployDatabaseService(
            InstallAddonsService installAddonsService,
            HerokuClient herokuClient,
            ConfigureLogDrainsService configureLogDrainsService,
            CreateBuildService createBuildService,
            Logger logger) {
        super(installAddonsService, herokuClient, configureLogDrainsService, createBuildService, logger)
    }

    void deploy(HerokuDatabaseApp app, int delayAfterDestroyApp) {
        maybeCreateApplication(app.name, app.teamName, app.recreate, app.stack, app.personalApp, delayAfterDestroyApp)
        installAddons(app.addons, app.name)
        configureLogDrainsService.configureLogDrains(app.logDrains, app.name, ADD_LOG_DRAINS_RETRIES)
        createBuildService.createBuild(app.buildSource, app.name)
        addConfig(app.herokuConfig, app.name)
        waitForAppFormation(app.name, app.buildSource)
        updateProcessFormation(app.name, app.herokuProcess)
        migrateDatabase(app)
        maybeDisableAcm(app)

        logger.lifecycle "Successfully deployed app ${app.name}"
    }

    void migrateDatabase(HerokuDatabaseApp app) {
        logger.lifecycle "Starting 'migrator' dyno to migrate database on ${app.name}"
        herokuClient.createDynoRequest(app.name, app.migrateCommand)

    }
}
