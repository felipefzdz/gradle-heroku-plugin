package com.felipefzdz.gradle.heroku.heroku.api

class CreateDynoRequest extends BaseHerokuApiJsonRequest<Void> {

    private final String appName
    private final String command

    CreateDynoRequest(String appName, String command) {
        this.appName = appName
        this.command = command
    }

    @Override
    String getEndpoint() {
        "/apps/${appName}/dynos"
    }

    @Override
    Map<String, Object> getBodyAsMap() {
        [command: command]
    }

}
