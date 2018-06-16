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

@CompileStatic
class DefaultHerokuClient implements HerokuClient {

    private HerokuAPI herokuAPI
    private String apiKey

    @Override
    HerokuClient init(String apiKey) {
        this.apiKey = apiKey
        this.herokuAPI = new HerokuAPI(apiKey)
        return this
    }

    @Override
    void destroyApp(String appName) {
        println "Destroying application $appName"
        herokuAPI.destroyApp(appName)
    }

    @Override
    void createApp(String appName, String teamName, boolean personalApp, String stack) {
        println "Creating heroku app $appName for team $teamName"
        api3(new OrganizationAppCreateRequest(appName, teamName, stack, personalApp))
    }

    @Override
    boolean appExists(String appName) {
        println "Checking if app $appName exists"
        herokuAPI.appExists(appName)
    }

    @Override
    AddonChange installAddon(String appName, String plan) {
        println "Installing adddon $plan for app $appName"
        api3(new AddonInstall(appName, plan))
    }

    @Override
    List<Map<String, ?>> getAddonAttachments(String appName) {
        println "Get adddon attachments for app $appName"
        api3(new GetAddonAttachmentsRequest(appName))
    }

    @Override
    Map<String, String> listConfig(String appName) {
        println "List config for app $appName"
        herokuAPI.listConfig(appName)
    }

    @Override
    List<Map<String, ?>> listLogDrains(String appName) {
        println "List log drains for app $appName"
        api3(new ListLogDrainsRequest(appName))
    }

    @Override
    void addLogDrain(String appName, String logDrain) {
        println "Add log drain $logDrain for app $appName"
        api3(new AddDrainRequest(appName, logDrain))
    }

    @Override
    void setBuildPack(String appName, String buildpackUrl) {
        println "Set build pack $buildpackUrl for app $appName"
        api3(new SetBuildpackRequest(appName, buildpackUrl))
    }

    @Override
    Map<String, ?> createBuild(String appName, String buildVersion, String buildUrl) {
        println "Create build $buildUrl with version $buildVersion for app $appName"
        api3(new CreateBuildRequest(appName, buildVersion, buildUrl))
    }

    @Override
    Map<String, ?> getBuildRequest(String appName, String buildId) {
        println "Get build request $buildId for app $appName"
        api3(new GetBuildRequest(appName, buildId))
    }

    @Override
    List<Map<String, ?>> listBuilds(String appName) {
        println "List builds for app $appName"
        api3(new ListBuildsRequest(appName))
    }

    @Override
    void updateConfig(String appName, Map<String, String> config) {
        println "Update config $config for app $appName"
        herokuAPI.updateConfig(appName, config)
    }

    @Override
    void enableFeature(String appName, String feature) {
        println "Enable feature $feature for app $appName"
        api3(new EnableFeature(appName, feature))

    }

    @Override
    Map<String, ?> getFeature(String appName, String feature) {
        println "Get feature $feature for app $appName"
        api3(new GetFeature(appName, feature))
    }

    @Override
    void createAddonAttachment(String appName, String addonId, String addonName) {
        println "Create addon attachment $addonName with id $addonId for app $appName"
        api3(new CreateAddonAttachmentRequest(appName, addonId, addonName))
    }

    @Override
    List<Map<String, ?>> getFormations(String appName) {
        println "Get formations for app $appName"
        api3(new GetFormationRequest(appName))
    }

    @Override
    void updateProcessFormations(String appName, HerokuProcess process) {
        println "Update process formation $process for app $appName"
        api3(new SetFormationRequest(appName, [process]))
    }

    @Override
    List<String> getCustomDomains(String appName) {
        println "Get custom domains for app $appName"
        api3(new ListDomainsRequest(appName)).findAll {
            it.kind == "custom"
        }.collect {
            it.hostname.toString()
        }
    }

    @Override
    void addDomain(String appName, String domainName) {
        println "Add domain $domainName for app $appName"
        api3(new DomainAdd(appName, domainName))
    }

    @Override
    void removeDomain(String appName, String domainName) {
        println "Remove domain $domainName for app $appName"
        api3(new DomainRemove(appName, domainName))
    }

    @Override
    Map<String, ?> getApp(String appName) {
        println "Get app $appName"
        api3(new AppInfo(appName))
    }

    @Override
    void disableAcm(String appName) {
        println "Disable ACM for app $appName"
        api3(new AppDisableAcmRequest(appName))
    }

    @Override
    void createDynoRequest(String appName, String migrateCommand) {
        println "Create dyno request $migrateCommand for app $appName"
        api3(new CreateDynoRequest(appName, migrateCommand))
    }

    private <T> T api3(Request<T> request) {
        herokuAPI.connection.execute(request, apiKey)
    }
}
