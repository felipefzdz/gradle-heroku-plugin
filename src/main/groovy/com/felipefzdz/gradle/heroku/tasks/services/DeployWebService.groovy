package com.felipefzdz.gradle.heroku.tasks.services

import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.model.HerokuAddonAttachment
import com.felipefzdz.gradle.heroku.tasks.model.HerokuWebApp
import com.felipefzdz.gradle.heroku.tasks.model.ReadinessProbe
import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.logging.Logger

import java.time.Duration

import static com.felipefzdz.gradle.heroku.utils.AsyncUtil.waitFor

@CompileStatic
class DeployWebService extends BaseDeployService {

    private final String PROXY_HEROKU_APP = System.getenv('GRADLE_HEROKU_PLUGIN_APP_PROXY')
    private final EnableFeaturesService enableFeaturesService
    private final AddAddonAttachmentsService addAddonAttachmentsService

    DeployWebService(
            InstallAddonsService installAddonsService,
            HerokuClient herokuClient,
            ConfigureLogDrainsService configureLogDrainsService,
            CreateBuildService createBuildService,
            EnableFeaturesService enableFeaturesService,
            AddAddonAttachmentsService addAddonAttachmentsService,
            Logger logger) {
        super(installAddonsService, herokuClient, configureLogDrainsService, createBuildService, logger)
        this.addAddonAttachmentsService = addAddonAttachmentsService
        this.enableFeaturesService = enableFeaturesService
    }

    void deploy(HerokuWebApp app, int delayAfterDestroyApp) {
        maybeCreateApplication(app.name, app.teamName, app.recreate, app.stack, app.personalApp, delayAfterDestroyApp)
        installAddons(app.addons, app.name)
        configureLogDrainsService.configureLogDrains(app.logDrains, app.name)
        createBuildService.createBuild(app.buildSource, app.name)
        addConfig(app.herokuConfig, app.name)
        enableFeatures(app)
        addAddonAttachments(app.addonAttachments, app.name)
        waitForAppFormation(app.name, app.buildSource)
        updateProcessFormation(app.name, app.herokuProcess)
        updateDomains(app)
        probeReadiness(app)
        maybeDisableAcm(app)

        logger.lifecycle "Successfully deployed app ${app.name}"
    }

    private enableFeatures(HerokuWebApp app) {
        enableFeaturesService.enableFeatures(app.features, app.name)
    }

    private void addAddonAttachments(NamedDomainObjectContainer<HerokuAddonAttachment> addonAttachments, String appName) {
        if (addonAttachments) {
            addAddonAttachmentsService.addAddonAttachments(addonAttachments.toList(), appName)
        }
    }

    private void updateDomains(HerokuWebApp app) {
        if (app.domains != null && !app.domains.isEmpty()) {
            logger.lifecycle "Fetching domain configuration"
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
            String urlAsString = PROXY_HEROKU_APP == null ? probe.url : "$PROXY_HEROKU_APP/version"
            URL url = new URL(urlAsString)
            logger.lifecycle("Fetch readiness endpoint $url...")
            delay(Duration.ofSeconds(9), SKIP_WAITS)
            waitFor(TIMEOUT, TEST_INTERVAL, "Readiness probe based on $url", SKIP_WAITS) {
                def json = new JsonSlurper().parse(url) as Map
                logger.lifecycle("Fetching $url")
                probe.command.execute(app, json)
            }
        }
    }

}
