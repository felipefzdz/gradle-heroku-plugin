package com.felipefzdz.gradle.heroku.tasks.services

import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.model.HerokuAddonAttachment
import com.felipefzdz.gradle.heroku.tasks.model.HerokuWebApp
import com.felipefzdz.gradle.heroku.tasks.model.ReadinessProbe
import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
import org.gradle.api.NamedDomainObjectContainer

import java.time.Duration

import static com.felipefzdz.gradle.heroku.utils.AsyncUtil.waitFor

@CompileStatic
class DeployWebService extends BaseDeployService {

    protected final EnableFeaturesService enableFeaturesService
    protected final AddAddonAttachmentsService addAddonAttachmentsService

    DeployWebService(
            InstallAddonsService installAddonsService,
            HerokuClient herokuClient,
            ConfigureLogDrainsService configureLogDrainsService,
            CreateBuildService createBuildService,
            EnableFeaturesService enableFeaturesService,
            AddAddonAttachmentsService addAddonAttachmentsService) {
        super(installAddonsService, herokuClient, configureLogDrainsService, createBuildService)
        this.addAddonAttachmentsService = addAddonAttachmentsService
        this.enableFeaturesService = enableFeaturesService
    }

    void deploy(HerokuWebApp app, int delayAfterDestroyApp, String apiKey) {
        herokuClient.init(apiKey)
        maybeCreateApplication(app.name, app.teamName, app.recreate, app.stack, app.personalApp, delayAfterDestroyApp)
        installAddons(app.addons, apiKey, app.name)
        configureLogDrainsService.configureLogDrains(app.logDrains, apiKey, app.name)
        createBuildService.createBuild(app.buildSource, apiKey, app.name)
        addConfig(app.config, app.name)
        enableFeatures(app, apiKey)
        addAddonAttachments(app.addonAttachments, apiKey, app.name)
        waitForAppFormation(app.name, app.buildSource)
        updateProcessFormation(app.name, app.herokuProcess)
        updateDomains(app)
        probeReadiness(app)
        maybeDisableAcm(app)

        println "Successfully deployed app ${app.name}"
    }

    private enableFeatures(HerokuWebApp app, String apiKey) {
        enableFeaturesService.enableFeatures(app.features, apiKey, app.name)
    }

    private void addAddonAttachments(NamedDomainObjectContainer<HerokuAddonAttachment> addonAttachments, String apiKey, String appName) {
        if (addonAttachments) {
            addAddonAttachmentsService.addAddonAttachments(addonAttachments.toList(), apiKey, appName)
        }
    }

    private void updateDomains(HerokuWebApp app) {
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

    private void probeReadiness(HerokuWebApp app) {
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

}
