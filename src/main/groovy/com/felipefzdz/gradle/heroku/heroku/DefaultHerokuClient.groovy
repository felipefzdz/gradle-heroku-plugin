package com.felipefzdz.gradle.heroku.heroku

import com.felipefzdz.gradle.heroku.heroku.api.*
import com.felipefzdz.gradle.heroku.tasks.model.HerokuProcess
import com.heroku.api.AddonChange
import com.heroku.api.HerokuAPI
import com.heroku.api.request.Request
import com.heroku.api.request.addon.AddonInstall
import com.heroku.api.request.domain.DomainAdd
import com.heroku.api.request.domain.DomainRemove
import groovy.transform.CompileStatic
import org.gradle.api.logging.Logger

@CompileStatic
class DefaultHerokuClient implements HerokuClient {

    private HerokuAPI herokuAPI
    private String apiKey
    private Logger logger

    DefaultHerokuClient(Logger logger) {
        this.logger = logger
    }

    @Override
    HerokuClient init(String apiKey) {
        this.apiKey = apiKey
        this.herokuAPI = new HerokuAPI(apiKey)
        return this
    }

    @Override
    void destroyApp(String appName) {
        logger.lifecycle "Destroying application $appName"
        herokuAPI.destroyApp(appName)
    }

    @Override
    void createApp(String appName, String teamName, boolean personalApp, String stack) {
        logger.lifecycle "Creating heroku app $appName for team $teamName"
        api3(new OrganizationAppCreateRequest(appName, teamName, stack, personalApp))
    }

    @Override
    boolean appExists(String appName) {
        logger.lifecycle "Checking if app $appName exists"
        herokuAPI.appExists(appName)
    }

    @Override
    AddonChange installAddon(String appName, String plan) {
        logger.lifecycle "Installing adddon $plan for app $appName"
        api3(new AddonInstall(appName, plan))
    }

    @Override
    List<Map<String, ?>> getAddonAttachments(String appName) {
        logger.lifecycle "Get adddon attachments for app $appName"
        api3(new GetAddonAttachmentsRequest(appName))
    }

    @Override
    Map<String, String> listConfig(String appName) {
        logger.lifecycle "List config for app $appName"
        herokuAPI.listConfig(appName)
    }

    @Override
    List<Map<String, ?>> listLogDrains(String appName) {
        logger.lifecycle "List log drains for app $appName"
        api3(new ListLogDrainsRequest(appName))
    }

    @Override
    void addLogDrain(String appName, String logDrain) {
        logger.lifecycle "Add log drain $logDrain for app $appName"
        api3(new AddDrainRequest(appName, logDrain))
    }

    @Override
    void setBuildPack(String appName, String buildpackUrl) {
        logger.lifecycle "Set build pack $buildpackUrl for app $appName"
        api3(new SetBuildpackRequest(appName, buildpackUrl))
    }

    @Override
    Map<String, ?> createBuild(String appName, String buildVersion, String buildUrl) {
        logger.lifecycle "Create build $buildUrl with version $buildVersion for app $appName"
        api3(new CreateBuildRequest(appName, buildVersion, buildUrl))
    }

    @Override
    Map<String, ?> getBuildRequest(String appName, String buildId) {
        logger.lifecycle "Get build request $buildId for app $appName"
        api3(new GetBuildRequest(appName, buildId))
    }

    @Override
    List<Map<String, ?>> listBuilds(String appName) {
        logger.lifecycle "List builds for app $appName"
        api3(new ListBuildsRequest(appName))
    }

    @Override
    void updateConfig(String appName, Map<String, String> config) {
        logger.lifecycle "Update config $config for app $appName"
        herokuAPI.updateConfig(appName, config)
    }

    @Override
    void enableFeature(String appName, String feature) {
        logger.lifecycle "Enable feature $feature for app $appName"
        api3(new EnableFeature(appName, feature))

    }

    @Override
    Map<String, ?> getFeature(String appName, String feature) {
        logger.lifecycle "Get feature $feature for app $appName"
        api3(new GetFeature(appName, feature))
    }

    @Override
    void createAddonAttachment(String appName, String addonId, String addonName) {
        logger.lifecycle "Create addon attachment $addonName with id $addonId for app $appName"
        api3(new CreateAddonAttachmentRequest(appName, addonId, addonName))
    }

    @Override
    List<Map<String, ?>> getFormations(String appName) {
        logger.lifecycle "Get formations for app $appName"
        api3(new GetFormationRequest(appName))
    }

    @Override
    void updateProcessFormations(String appName, HerokuProcess process) {
        logger.lifecycle "Update process formation $process for app $appName"
        api3(new SetFormationRequest(appName, [process]))
    }

    @Override
    List<String> getCustomDomains(String appName) {
        logger.lifecycle "Get custom domains for app $appName"
        api3(new ListDomainsRequest(appName)).findAll {
            it.kind == "custom"
        }.collect {
            it.hostname.toString()
        }
    }

    @Override
    void addDomain(String appName, String domainName) {
        logger.lifecycle "Add domain $domainName for app $appName"
        api3(new DomainAdd(appName, domainName))
    }

    @Override
    void removeDomain(String appName, String domainName) {
        logger.lifecycle "Remove domain $domainName for app $appName"
        api3(new DomainRemove(appName, domainName))
    }

    @Override
    Map<String, ?> getApp(String appName) {
        logger.lifecycle "Get app $appName"
        api3(new AppInfo(appName))
    }

    @Override
    void disableAcm(String appName) {
        logger.lifecycle "Disable ACM for app $appName"
        api3(new AppDisableAcmRequest(appName))
    }

    @Override
    void createDynoRequest(String appName, String migrateCommand) {
        logger.lifecycle "Create dyno request $migrateCommand for app $appName"
        api3(new CreateDynoRequest(appName, migrateCommand))
    }

    private <T> T api3(Request<T> request) {
        herokuAPI.connection.execute(request, apiKey)
    }
}
