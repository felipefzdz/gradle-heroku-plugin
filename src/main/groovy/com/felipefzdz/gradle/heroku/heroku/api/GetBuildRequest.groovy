package com.felipefzdz.gradle.heroku.heroku.api

class GetBuildRequest extends BaseHerokuApiRequest<Map<String, ?>> {

    private final String buildId
    private final String appName

    GetBuildRequest(String appName, String buildId) {
        this.appName = appName
        this.buildId = buildId
    }

    @Override
    String getEndpoint() {
        "/apps/$appName/builds/$buildId"
    }

}
