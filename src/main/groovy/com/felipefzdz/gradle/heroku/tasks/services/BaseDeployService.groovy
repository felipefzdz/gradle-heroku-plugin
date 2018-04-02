package com.felipefzdz.gradle.heroku.tasks.services

import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.model.*
import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
import org.gradle.api.NamedDomainObjectContainer

import java.time.Duration

import static com.felipefzdz.gradle.heroku.utils.AsyncUtil.waitFor

@CompileStatic
class BaseDeployService {

    protected static final Duration TIMEOUT = Duration.ofMinutes(6)
    protected static final Duration TEST_INTERVAL = Duration.ofSeconds(1)

    protected final InstallAddonsService installAddonsService
    protected final HerokuClient herokuClient
    protected final ConfigureLogDrainsService configureLogDrainsService
    protected final CreateBuildService createBuildService

    BaseDeployService(
            InstallAddonsService installAddonsService,
            HerokuClient herokuClient,
            ConfigureLogDrainsService configureLogDrainsService,
            CreateBuildService createBuildService) {
        this.installAddonsService = installAddonsService
        this.herokuClient = herokuClient
        this.configureLogDrainsService = configureLogDrainsService
        this.createBuildService = createBuildService
    }

   

    protected void waitForAppFormation(String appName, BuildSource buildSource) {
        if (buildSource) {
            println "Checking for existence of resource formation …"
            waitFor(Duration.ofMinutes(5), Duration.ofSeconds(3), "waiting for process formation for $appName") {
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

    protected void installAddons(NamedDomainObjectContainer<HerokuAddon> addons, String apiKey, String appName) {
        if (addons) {
            installAddonsService.installAddons(addons.toList(), apiKey, appName)
        }
    }



    protected void addConfig(Map<String, String> config, String appName) {
        if (config) {
            println "Setting env config variables ${config.keySet().toList().sort()}"
            herokuClient.updateConfig(appName, config)
            println "Added environment config for $appName"
        }
    }

    protected void updateProcessFormation(String appName, HerokuProcess process) {
        println "Updating process formations for app ${appName}"
        if (process != null) {
            herokuClient.updateProcessFormations(appName, process)
        } else {
            println "No process formations for app ${appName}"
        }
    }

    def maybeDisableAcm(HerokuApp app) {
        if (app.disableAcm) {
            herokuClient.disableAcm(app.name)
        }
    }

    protected delay(Duration duration) {
        println "Delaying for ${duration.toMillis()} milliseconds..."
        sleep(duration.toMillis())
    }

}
