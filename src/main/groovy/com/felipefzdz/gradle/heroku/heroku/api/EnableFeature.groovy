package com.felipefzdz.gradle.heroku.heroku.api

import com.heroku.api.http.Http

class EnableFeature extends BaseHerokuApiJsonRequest<Void> {

    private final String appName
    private final String featureName

    EnableFeature(String appName, String featureName) {
        this.appName = appName
        this.featureName = featureName
    }

    @Override
    Http.Method getHttpMethod() {
        Http.Method.PATCH
    }

    @Override
    String getEndpoint() {
        "/apps/$appName/features/$featureName"
    }

    @Override
    Map<String, Object> getBodyAsMap() {
        [enabled: true]
    }

}
