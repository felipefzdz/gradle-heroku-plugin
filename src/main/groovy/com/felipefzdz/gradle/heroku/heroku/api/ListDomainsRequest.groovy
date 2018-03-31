package com.felipefzdz.gradle.heroku.heroku.api

class ListDomainsRequest extends BaseHerokuApiRequest<List<Map<String, ?>>> {

    private final String appName

    ListDomainsRequest(String appName) {
        this.appName = appName
    }

    @Override
    String getEndpoint() {
        "/apps/$appName/domains"
    }

}
