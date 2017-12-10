package com.felipefzdz.gradle.heroku.heroku.api

class ListBuildsRequest extends BaseHerokuApiRequest<List<Map<String, ?>>> {

    private final String appName

    ListBuildsRequest(String appName) {
        this.appName = appName
    }

    @Override
    String getEndpoint() {
        "/apps/$appName/builds"
    }

}
