package com.felipefzdz.gradle.heroku.dependencyinjection

import com.felipefzdz.gradle.heroku.heroku.DefaultHerokuClient
import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.services.*
import org.gradle.api.logging.Logger

class Graph {
    static private HerokuClient herokuClient
    static private InstallAddonsService installAddonsService
    static private ConfigureLogDrainsService configureLogDrainsService
    static private CreateBuildService createBuildService
    static private EnableFeaturesService enableFeaturesService
    static private AddAddonAttachmentsService addAddonAttachmentsService
    static private CreateAppService createAppService
    static private DestroyAppService destroyAppService
    static private DeployWebService deployWebService
    static private DeployDatabaseService deployDatabaseService
    static private VerifyConfigService verifyConfigService

    static void init(Logger logger) {
        herokuClient = new DefaultHerokuClient(logger)
        installAddonsService = new InstallAddonsService(herokuClient, logger)
        configureLogDrainsService = new ConfigureLogDrainsService(herokuClient, logger)
        createBuildService = new CreateBuildService(herokuClient, logger)
        enableFeaturesService = new EnableFeaturesService(herokuClient, logger)
        addAddonAttachmentsService = new AddAddonAttachmentsService(herokuClient, logger)
        createAppService = new CreateAppService(herokuClient, logger)
        destroyAppService = new DestroyAppService(herokuClient, logger)
        verifyConfigService = new VerifyConfigService(herokuClient, logger)
        deployWebService =
                new DeployWebService(installAddonsService, herokuClient, configureLogDrainsService,
                        createBuildService, enableFeaturesService, addAddonAttachmentsService, logger)
        deployDatabaseService =
                new DeployDatabaseService(installAddonsService, herokuClient, configureLogDrainsService,
                        createBuildService, logger)
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

    static DeployWebService getDeployWebService() {
        return deployWebService
    }

    static DeployDatabaseService getDeployDatabaseService() {
        return deployDatabaseService
    }

    static VerifyConfigService getVerifyConfigService() {
        return verifyConfigService
    }
}
