package com.felipefzdz.gradle.heroku.heroku

import com.heroku.api.AddonChange

interface HerokuClient {

    HerokuClient init(String apiKey)

    void destroyApp(String appName)

    void createApp(String appName, String teamName, boolean personalApp, String stack)

    boolean appExists(String appName)

    AddonChange installAddon(String appName, String plan)

    List<Map<String,?>> getAddonAttachments(String appName)
}