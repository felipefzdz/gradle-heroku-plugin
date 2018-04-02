package com.felipefzdz.gradle.heroku.heroku

import com.felipefzdz.gradle.heroku.heroku.api.AddDrainRequest
import com.felipefzdz.gradle.heroku.heroku.api.AppDisableAcmRequest
import com.felipefzdz.gradle.heroku.heroku.api.AppInfo
import com.felipefzdz.gradle.heroku.heroku.api.CreateAddonAttachmentRequest
import com.felipefzdz.gradle.heroku.heroku.api.CreateBuildRequest
import com.felipefzdz.gradle.heroku.heroku.api.CreateDynoRequest
import com.felipefzdz.gradle.heroku.heroku.api.EnableFeature
import com.felipefzdz.gradle.heroku.heroku.api.GetAddonAttachmentsRequest
import com.felipefzdz.gradle.heroku.heroku.api.GetBuildRequest
import com.felipefzdz.gradle.heroku.heroku.api.GetFeature
import com.felipefzdz.gradle.heroku.heroku.api.GetFormationRequest
import com.felipefzdz.gradle.heroku.heroku.api.ListBuildsRequest
import com.felipefzdz.gradle.heroku.heroku.api.ListDomainsRequest
import com.felipefzdz.gradle.heroku.heroku.api.ListLogDrainsRequest
import com.felipefzdz.gradle.heroku.heroku.api.OrganizationAppCreateRequest
import com.felipefzdz.gradle.heroku.heroku.api.SetBuildpackRequest
import com.felipefzdz.gradle.heroku.heroku.api.SetFormationRequest
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
        if (herokuAPI == null) {
            this.apiKey = apiKey
            this.herokuAPI = new HerokuAPI(apiKey)
        }
        this
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
        herokuAPI.appExists(appName)
    }

    @Override
    AddonChange installAddon(String appName, String plan) {
        api3(new AddonInstall(appName, plan))
    }

    @Override
    List<Map<String, ?>> getAddonAttachments(String appName) {
        api3(new GetAddonAttachmentsRequest(appName))
    }

    @Override
    Map<String, String> listConfig(String appName) {
        herokuAPI.listConfig(appName)
    }

    @Override
    List<Map<String, ?>> listLogDrains(String appName) {
        api3(new ListLogDrainsRequest(appName))
    }

    @Override
    void addLogDrain(String appName, String logDrain) {
        api3(new AddDrainRequest(appName, logDrain))
    }

    @Override
    void setBuildPack(String appName, String buildpackUrl) {
        api3(new SetBuildpackRequest(appName, buildpackUrl))
    }

    @Override
    Map<String, ?> createBuild(String appName, String buildVersion, String buildUrl) {
        api3(new CreateBuildRequest(appName, buildVersion, buildUrl))
    }

    @Override
    Map<String, ?> getBuildRequest(String appName, String buildId) {
        api3(new GetBuildRequest(appName, buildId))
    }

    @Override
    List<Map<String, ?>> listBuilds(String appName) {
        api3(new ListBuildsRequest(appName))
    }

    @Override
    void updateConfig(String appName, Map<String, String> config) {
        herokuAPI.updateConfig(appName, config)
    }

    @Override
    void enableFeature(String appName, String feature) {
        api3(new EnableFeature(appName, feature))

    }

    @Override
    Map<String, ?> getFeature(String appName, String feature) {
        api3(new GetFeature(appName, feature))
    }

    @Override
    void createAddonAttachment(String appName, String addonId, String addonName) {
        api3(new CreateAddonAttachmentRequest(appName, addonId, addonName))
    }

    @Override
    List<Map<String, ?>> getFormations(String appName) {
        api3(new GetFormationRequest(appName))
    }

    @Override
    void updateProcessFormations(String appName, HerokuProcess process) {
        api3(new SetFormationRequest(appName, [process]))
    }

    @Override
    List<String> getCustomDomains(String appName) {
        api3(new ListDomainsRequest(appName)).findAll {
            it.kind == "custom"
        }.collect {
            it.hostname.toString()
        }
    }

    @Override
    void addDomain(String appName, String domainName) {
        api3(new DomainAdd(appName, domainName))
    }

    @Override
    void removeDomain(String appName, String domainName) {
        api3(new DomainRemove(appName, domainName))
    }

    @Override
    Map<String, ?> getApp(String appName) {
        api3(new AppInfo(appName))
    }

    @Override
    void disableAcm(String appName) {
        api3(new AppDisableAcmRequest(appName))
    }

    @Override
    void createDynoRequest(String appName, String migrateCommand) {
        api3(new CreateDynoRequest(appName, migrateCommand))
    }

    private <T> T api3(Request<T> request) {
        herokuAPI.connection.execute(request, apiKey)
    }
}
