package com.felipefzdz.gradle.heroku.heroku.api

class ListLogDrainsRequest extends BaseHerokuApiRequest<List<Map<String, ?>>> {

    private final String appName

    ListLogDrainsRequest(String appName) {
        this.appName = appName
    }

    @Override
    String getEndpoint() {
        "/apps/$appName/log-drains"
    }

}
