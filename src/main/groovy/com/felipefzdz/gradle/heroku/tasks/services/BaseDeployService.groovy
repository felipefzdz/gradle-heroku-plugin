package com.felipefzdz.gradle.heroku.tasks.services

import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.model.BuildSource
import com.felipefzdz.gradle.heroku.tasks.model.HerokuAddon
import com.felipefzdz.gradle.heroku.tasks.model.HerokuApp
import com.felipefzdz.gradle.heroku.tasks.model.HerokuConfig
import com.felipefzdz.gradle.heroku.tasks.model.HerokuProcess
import groovy.transform.CompileStatic
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.logging.Logger

import java.time.Duration

import static com.felipefzdz.gradle.heroku.utils.AsyncUtil.waitFor

@CompileStatic
class BaseDeployService {

    protected final Boolean SKIP_WAITS = Boolean.valueOf(System.getenv('GRADLE_HEROKU_PLUGIN_SKIP_WAITS'))
    protected static final Duration TIMEOUT = Duration.ofMinutes(6)
    protected static final Duration TEST_INTERVAL = Duration.ofSeconds(1)

    protected final InstallAddonsService installAddonsService
    protected final HerokuClient herokuClient
    protected final ConfigureLogDrainsService configureLogDrainsService
    protected final CreateBuildService createBuildService
    protected final Logger logger

    BaseDeployService(
            InstallAddonsService installAddonsService,
            HerokuClient herokuClient,
            ConfigureLogDrainsService configureLogDrainsService,
            CreateBuildService createBuildService,
            Logger logger) {
        this.installAddonsService = installAddonsService
        this.herokuClient = herokuClient
        this.configureLogDrainsService = configureLogDrainsService
        this.createBuildService = createBuildService
        this.logger = logger
    }


    protected void waitForAppFormation(String appName, BuildSource buildSource) {
        if (buildSource) {
            logger.lifecycle "Checking for existence of resource formation …"
            waitFor(Duration.ofMinutes(5), Duration.ofSeconds(3), "waiting for process formation for $appName", SKIP_WAITS) {
                def formation = herokuClient.getFormations(appName)
                assert !formation.isEmpty()
            }
        }
    }

    protected void maybeCreateApplication(String appName, String teamName, Boolean recreate, String stack, Boolean personalApp, int delayAfterDestroyApp) {
        boolean exists = herokuClient.appExists(appName)
        if (exists && recreate) {
            herokuClient.destroyApp(appName)
            // Give some time to Heroku to actually destroy the app
            delay(Duration.ofSeconds(delayAfterDestroyApp), SKIP_WAITS)
            exists = false
        }
        if (exists) {
            logger.lifecycle "App $appName already exists and won't be created"
        } else {
            herokuClient.createApp(appName, teamName, personalApp, stack)
            logger.lifecycle "Successfully created app $appName"
        }
    }

    protected void installAddons(NamedDomainObjectContainer<HerokuAddon> addons, String appName) {
        if (addons) {
            installAddonsService.installAddons(addons.toList(), appName)
        }
    }

    protected void addConfig(HerokuConfig herokuConfig, String appName) {
        Map<String, String> config = herokuConfig != null ? herokuConfig.configToBeExpected : null
        if (config) {
            logger.lifecycle "Setting env config variables ${config.keySet().toList().sort()}"
            herokuClient.updateConfig(appName, config)
            logger.lifecycle "Added environment config for $appName"
        }
    }

    protected void updateProcessFormation(String appName, HerokuProcess process) {
        logger.lifecycle "Updating process formations for app ${appName}"
        if (process != null) {
            herokuClient.updateProcessFormations(appName, process)
        } else {
            logger.lifecycle "No process formations for app ${appName}"
        }
    }

    def maybeDisableAcm(HerokuApp app) {
        if (app.disableAcm) {
            herokuClient.disableAcm(app.name)
        }
    }

    protected delay(Duration duration, boolean skipWaits) {
        if (!skipWaits) {
            logger.lifecycle "Delaying for ${duration.toMillis()} milliseconds..."
            sleep(duration.toMillis())
        }

    }

}
