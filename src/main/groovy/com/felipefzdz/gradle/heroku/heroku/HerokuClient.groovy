package com.felipefzdz.gradle.heroku.heroku

import com.felipefzdz.gradle.heroku.tasks.model.HerokuProcess
import com.heroku.api.AddonChange

interface HerokuClient {

    void destroyApp(String appName)

    void createApp(String appName, String teamName, boolean personalApp, String stack)

    boolean appExists(String appName)

    AddonChange installAddon(String appName, String plan, Map<String, String> config)

    List<Map<String, ?>> getAddonAttachments(String appName)

    Map<String, String> listConfig(String appName)

    List<Map<String, ?>> listLogDrains(String appName)

    void addLogDrain(String appName, String logDrain)

    void setBuildPack(String appName, String buildpackUrl)

    Map<String, ?> createBuild(String appName, String buildVersion, String buildUrl)

    Map<String, ?> getBuildRequest(String appName, String buildId)

    List<Map<String, ?>> listBuilds(String appName)

    void updateConfig(String appName, Map<String, String> config)

    void enableFeature(String appName, String feature)

    Map<String, ?> getFeature(String appName, String feature)

    void createAddonAttachment(String appName, String addonId, String addonName)

    List<Map<String, ?>> getFormations(String appName)

    void updateProcessFormations(String appName, HerokuProcess process)

    List<String> getCustomDomains(String appName)

    void addDomain(String appName, String domainName)

    void removeDomain(String appName, String domainName)

    Map<String, ?> getApp(String appName)

    void disableAcm(String appName)

    void createDynoRequest(String appName, String migrateCommand)

    HerokuClient init(String apiKey)
}