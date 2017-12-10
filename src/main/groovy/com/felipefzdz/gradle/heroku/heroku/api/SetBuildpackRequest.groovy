package com.felipefzdz.gradle.heroku.heroku.api

import com.heroku.api.http.Http

class SetBuildpackRequest extends BaseHerokuApiJsonRequest<Void> {

    private final String buildpackUrl
    private final String appName

    SetBuildpackRequest(String appName, String buildpackUrl) {
        this.appName = appName
        this.buildpackUrl = buildpackUrl
    }

    @Override
    Http.Method getHttpMethod() {
        Http.Method.PUT
    }

    @Override
    String getEndpoint() {
        "/apps/$appName/buildpack-installations"
    }

    @Override
    Map<String, Object> getBodyAsMap() {
        [updates: [[buildpack: buildpackUrl]]]
    }

}
