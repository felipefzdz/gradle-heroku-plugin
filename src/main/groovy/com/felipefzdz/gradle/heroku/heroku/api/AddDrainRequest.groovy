package com.felipefzdz.gradle.heroku.heroku.api

class AddDrainRequest extends BaseHerokuApiJsonRequest<Void> {

    private final String drain
    private final String appName

    AddDrainRequest(String appName, String drain) {
        this.appName = appName
        this.drain = drain
    }

    @Override
    String getEndpoint() {
        "/apps/$appName/log-drains"
    }

    @Override
    Map<String, Object> getBodyAsMap() {
        [url: drain]
    }

}
