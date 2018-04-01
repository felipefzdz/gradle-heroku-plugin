package com.felipefzdz.gradle.heroku.tasks.services

import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.model.*
import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
import org.gradle.api.NamedDomainObjectContainer

import java.time.Duration

import static com.felipefzdz.gradle.heroku.utils.AsyncUtil.waitFor

@CompileStatic
class DeployService {

    private static final Duration TIMEOUT = Duration.ofMinutes(6)
    private static final Duration TEST_INTERVAL = Duration.ofSeconds(1)

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
        herokuClient.init(apiKey)
        maybeCreateApplication(app.name, app.teamName, app.recreate, app.stack, app.personalApp, delayAfterDestroyApp)
        installAddons(app.addons, apiKey, app.name)
        configureLogDrainsService.configureLogDrains(app.logDrains, apiKey, app.name)
        createBuildService.createBuild(app.buildSource, apiKey, app.name)
        addConfig(app.config, app.name)
        enableFeaturesService.enableFeatures(app.features, apiKey, app.name)
        addAddonAttachments(app.addonAttachments, apiKey, app.name)
        waitForAppFormation(app.name, app.buildSource)
        updateProcessFormation(app.name, app.herokuProcess)
        updateDomains(app)
        probeReadiness(app)

        println "Successfully deployed app ${app.name}"
    }

    private void waitForAppFormation(String appName, BuildSource buildSource) {
        if (buildSource) {
            println "Checking for existence of resource formation …"
            waitFor(Duration.ofMinutes(5), Duration.ofSeconds(3), "waiting for process formation for $appName") {
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

    private void updateProcessFormation(String appName, HerokuProcess process) {
        println "Updating process formations for app ${appName}"
        if (process != null) {
            herokuClient.updateProcessFormations(appName, process)
        } else {
            println "No process formations for app ${appName}"
        }
    }


    private void updateDomains(HerokuApp app) {
        if (app.domains != null && !app.domains.isEmpty()) {
            println "Fetching domain configuration"
            def domains = herokuClient.getCustomDomains(app.name)

            app.domains.each {
                if (!(it in domains)) {
                    herokuClient.addDomain(app.name, it)
                }
            }
            domains.each {
                if (!(it in app.domains)) {
                    herokuClient.removeDomain(app.name, it)
                }
            }
        }
    }

    private void probeReadiness(HerokuApp app) {
        ReadinessProbe probe = app.readinessProbe
        if (probe != null) {
            String proxyHerokuApp = System.getenv('HEROKU_APP_PROXY')
            String urlAsString = proxyHerokuApp == null ? probe.url : "$proxyHerokuApp/version"
            URL url = new URL(urlAsString)
            println("Fetch readiness endpoint $url...")
            delay(Duration.ofSeconds(9))
            waitFor(TIMEOUT, TEST_INTERVAL, "Readiness probe based on $url") {
                def json = new JsonSlurper().parse(url) as Map
                println("Fetching $url")
                probe.command.execute(app, json)
            }
        }
    }

    private delay(Duration duration) {
        println "Delaying for ${duration.toMillis()} milliseconds..."
        sleep(duration.toMillis())
    }

}
