package com.felipefzdz.gradle.heroku.dependencyinjection

import com.felipefzdz.gradle.heroku.heroku.DefaultHerokuClient
import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.services.*

class Graph {
    static private HerokuClient herokuClient
    static private InstallAddonsService installAddonsService
    static private ConfigureLogDrainsService configureLogDrainsService
    static private CreateBuildService createBuildService
    static private EnableFeaturesService enableFeaturesService
    static private AddAddonAttachmentsService addAddonAttachmentsService
    static private CreateAppService createAppService
    static private DestroyAppService destroyAppService
    static private DeployService deployService

    static void init() {
        herokuClient = new DefaultHerokuClient()
        installAddonsService = new InstallAddonsService(herokuClient)
        configureLogDrainsService = new ConfigureLogDrainsService(herokuClient)
        createBuildService = new CreateBuildService(herokuClient)
        enableFeaturesService = new EnableFeaturesService(herokuClient)
        addAddonAttachmentsService = new AddAddonAttachmentsService(herokuClient)
        createAppService = new CreateAppService(herokuClient)
        destroyAppService = new DestroyAppService(herokuClient)
        deployService =
                new DeployService(installAddonsService, herokuClient, configureLogDrainsService,
                        createBuildService, enableFeaturesService, addAddonAttachmentsService)
    }

    static HerokuClient getHerokuClient() {
        return herokuClient
    }

    static InstallAddonsService getInstallAddonsService() {
        return installAddonsService
    }

    static ConfigureLogDrainsService getConfigureLogDrainsService() {
        return configureLogDrainsService
    }

    static CreateBuildService getCreateBuildService() {
        return createBuildService
    }

    static EnableFeaturesService getEnableFeaturesService() {
        return enableFeaturesService
    }

    static AddAddonAttachmentsService getAddAddonAttachmentsService() {
        return addAddonAttachmentsService
    }

    static CreateAppService getCreateAppService() {
        return createAppService
    }

    static DestroyAppService getDestroyAppService() {
        return destroyAppService
    }

    static DeployService getDeployService() {
        return deployService
    }
}
