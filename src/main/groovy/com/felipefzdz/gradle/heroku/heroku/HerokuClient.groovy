package com.felipefzdz.gradle.heroku.heroku

import com.heroku.api.AddonChange

interface HerokuClient {

    HerokuClient init(String apiKey)

    void destroyApp(String appName)

    void createApp(String appName, String teamName, boolean personalApp, String stack)

    boolean appExists(String appName)

    AddonChange installAddon(String appName, String plan)

    List<Map<String,?>> getAddonAttachments(String appName)

    Map<String, String> listConfig(String appName)

    List<Map<String,?>> listLogDrains(String appName)

    void addLogDrain(String appName, String logDrain)

    void setBuildPack(String appName, String buildpackUrl)

    Map<String, ?> createBuild(String appName, String buildVersion, String buildUrl)

    Map<String, ?> getBuildRequest(String appName, String buildId)

    List<Map<String, ?>> listBuilds(String appName)

    void updateConfig(String appName, Map<String, String> config)

    void enableFeature(String appName, String feature)

    Map<String, ?> getFeature(String appName, String feature)
}