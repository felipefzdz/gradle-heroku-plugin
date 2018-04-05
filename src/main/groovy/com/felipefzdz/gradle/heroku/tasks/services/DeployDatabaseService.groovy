package com.felipefzdz.gradle.heroku.tasks.services

import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.model.HerokuDatabaseApp
import groovy.transform.CompileStatic

@CompileStatic
class DeployDatabaseService extends BaseDeployService {

    DeployDatabaseService(
            InstallAddonsService installAddonsService,
            HerokuClient herokuClient,
            ConfigureLogDrainsService configureLogDrainsService,
            CreateBuildService createBuildService) {
        super(installAddonsService, herokuClient, configureLogDrainsService, createBuildService)
    }

    void deploy(HerokuDatabaseApp app, int delayAfterDestroyApp) {
        maybeCreateApplication(app.name, app.teamName, app.recreate, app.stack, app.personalApp, delayAfterDestroyApp)
        installAddons(app.addons, app.name)
        configureLogDrainsService.configureLogDrains(app.logDrains, app.name)
        createBuildService.createBuild(app.buildSource, app.name)
        addConfig(app.herokuConfig, app.name)
        waitForAppFormation(app.name, app.buildSource)
        updateProcessFormation(app.name, app.herokuProcess)
        migrateDatabase(app)
        maybeDisableAcm(app)

        println "Successfully deployed app ${app.name}"
    }

    void migrateDatabase(HerokuDatabaseApp app) {
        println "Starting 'migrator' dyno to migrate database on ${app.name}"
        herokuClient.createDynoRequest(app.name, app.migrateCommand)

    }
}
