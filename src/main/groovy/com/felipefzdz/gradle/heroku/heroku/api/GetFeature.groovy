package com.felipefzdz.gradle.heroku.heroku.api

class GetFeature extends BaseHerokuApiRequest<Map<String, ?>> {

    private final String appName
    private final String featureName

    GetFeature(String appName, String featureName) {
        this.appName = appName
        this.featureName = featureName
    }

    @Override
    String getEndpoint() {
        "/apps/$appName/features/$featureName"
    }

}
