package com.felipefzdz.gradle.heroku.tasks.model

import com.felipefzdz.gradle.heroku.tasks.services.DeployDatabaseService
import groovy.transform.CompileStatic
import org.gradle.api.NamedDomainObjectContainer

@CompileStatic
class HerokuDatabaseApp extends HerokuApp {

    String migrateCommand

    DeployDatabaseService deployDatabaseService

    HerokuDatabaseApp(String name, DeployDatabaseService deployDatabaseService, NamedDomainObjectContainer<HerokuAddon> addons) {
        super(name, addons)
        this.deployDatabaseService = deployDatabaseService
    }

    @Override
    void deploy(int delayAfterDestroyApp, String apiKey) {
        deployDatabaseService.deploy(this, delayAfterDestroyApp, apiKey)
    }

    HerokuDatabaseApp migrateCommand(String migrateCommand) {
        this.migrateCommand = migrateCommand
        this
    }
}
