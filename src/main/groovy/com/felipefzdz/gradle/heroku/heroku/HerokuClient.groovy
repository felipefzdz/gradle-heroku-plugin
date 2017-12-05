package com.felipefzdz.gradle.heroku.heroku

interface HerokuClient {

    HerokuClient init(String apiKey)

    void destroyApp(String appName)

    void createApp(String appName, String teamName, boolean personalApp)

    boolean appExists(String appName)
}