package com.felipefzdz.gradle.heroku.tasks.services

import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.model.BuildSource
import com.felipefzdz.gradle.heroku.tasks.model.HerokuAddon
import com.felipefzdz.gradle.heroku.tasks.model.HerokuAddonAttachment
import com.felipefzdz.gradle.heroku.tasks.model.HerokuApp
import com.felipefzdz.gradle.heroku.tasks.model.HerokuProcess
import com.felipefzdz.gradle.heroku.utils.AsyncUtil
import groovy.transform.CompileStatic
import org.gradle.api.NamedDomainObjectContainer

import java.time.Duration

@CompileStatic
class DeployService {

    private final InstallAddonsService installAddonsService
    private final HerokuClient herokuClient
    private final ConfigureLogDrainsService configureLogDrainsService
    private final CreateBuildService createBuildService
    private final EnableFeaturesService enableFeaturesService
    private final AddAddonAttachmentsService addAddonAttachmentsService

    DeployService(
            InstallAddonsService installAddonsService,
            HerokuClient herokuClient,
            ConfigureLogDrainsService configureLogDrainsService,
            CreateBuildService createBuildService,
            EnableFeaturesService enableFeaturesService,
            AddAddonAttachmentsService addAddonAttachmentsService) {
        this.installAddonsService = installAddonsService
        this.herokuClient = herokuClient
        this.configureLogDrainsService = configureLogDrainsService
        this.createBuildService = createBuildService
        this.enableFeaturesService = enableFeaturesService
        this.addAddonAttachmentsService = addAddonAttachmentsService
    }

    void deploy(HerokuApp app, int delayAfterDestroyApp, String apiKey) {
        maybeCreateApplication(app.name, app.teamName, app.recreate, app.stack, app.personalApp, delayAfterDestroyApp)
        installAddons(app.addons, apiKey, app.name)
        configureLogDrainsService.configureLogDrains(app.logDrains, apiKey, app.name)
        createBuildService.createBuild(app.buildSource, apiKey, app.name)
        addConfig(app.config, app.name)
        enableFeaturesService.enableFeatures(app.features, apiKey, app.name)
        addAddonAttachments(app.addonAttachments, apiKey, app.name)
        waitForAppFormation(app.name, app.buildSource)
        updateProcessFormations(app.name, app.processes)

        println "Successfully deployed app ${app.name}"
    }

    private void waitForAppFormation(String appName, BuildSource buildSource) {
        if (buildSource) {
            println "Checking for existence of resource formation …"
            AsyncUtil.waitFor(Duration.ofMinutes(5), Duration.ofSeconds(3), "waiting for process formation for $appName") {
                def formation = herokuClient.getFormations(appName)
                assert !formation.isEmpty()
            }
        }
    }

    private void maybeCreateApplication(String appName, String teamName, boolean recreate, String stack, boolean personalApp, int delayAfterDestroyApp) {
        boolean exists = herokuClient.appExists(appName)
        if (exists && recreate) {
            herokuClient.destroyApp(appName)
            // Give some time to Heroku to actually destroy the app
            delay(Duration.ofSeconds(delayAfterDestroyApp))
            exists = false
        }
        if (exists) {
            println "App $appName already exists and won't be created"
        } else {
            herokuClient.createApp(appName, teamName, personalApp, stack)
            println "Successfully created app $appName"
        }
    }

    private void installAddons(NamedDomainObjectContainer<HerokuAddon> addons, String apiKey, String appName) {
        if (addons) {
            installAddonsService.installAddons(addons.toList(), apiKey, appName)
        }
    }

    private void addAddonAttachments(NamedDomainObjectContainer<HerokuAddonAttachment> addonAttachments, String apiKey, String appName) {
        if (addonAttachments) {
            addAddonAttachmentsService.addAddonAttachments(addonAttachments.toList(), apiKey, appName)
        }
    }

    private void addConfig(Map<String, String> config, String appName) {
        if (config) {
            println "Setting env config variables ${config.keySet().toList().sort()}"
            herokuClient.updateConfig(appName, config)
            println "Added environment config for $appName"
        }
    }

    private void updateProcessFormations(String appName, List<HerokuProcess> processes) {
        println "Updating process formations for app ${appName}"
        if (!processes.isEmpty()) {
            herokuClient.updateProcessFormations(appName, processes)
        } else {
            println "No process formations for app ${appName}"
        }
    }

    private delay(Duration duration) {
        println "Delaying for ${duration.toMillis()} milliseconds..."
        sleep(duration.toMillis())
    }
}
