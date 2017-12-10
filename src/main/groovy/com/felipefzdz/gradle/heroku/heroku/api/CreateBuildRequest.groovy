package com.felipefzdz.gradle.heroku.heroku.api

class CreateBuildRequest extends BaseHerokuApiJsonRequest<Map<String, ?>> {

    private final String appName
    private String url
    private String version

    CreateBuildRequest(String appName, String version, String url) {
        this.appName = appName
        this.version = version
        this.url = url
    }

    @Override
    String getEndpoint() {
        "/apps/$appName/builds"
    }

    @Override
    Map<String, Object> getBodyAsMap() {
        [source_blob: [url: url, version: version, version_description: "Build $version"]]
    }

}
