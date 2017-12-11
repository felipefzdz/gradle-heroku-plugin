package com.felipefzdz.gradle.heroku.heroku.api

class GetFormationRequest extends BaseHerokuApiRequest<List<Map<String, ?>>> {

    private final String appName

    GetFormationRequest(String appName) {
        this.appName = appName
    }

    @Override
    String getEndpoint() {
        "/apps/$appName/formation"
    }

}
