package com.felipefzdz.gradle.heroku.heroku.api

class AppInfo extends BaseHerokuApiRequest<Map<String, ?>> {

    private final String appName

    AppInfo(String appName) {
        this.appName = appName
    }

    @Override
    String getEndpoint() {
        "/apps/$appName"
    }

}
