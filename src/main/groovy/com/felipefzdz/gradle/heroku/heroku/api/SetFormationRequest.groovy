package com.felipefzdz.gradle.heroku.heroku.api

import com.felipefzdz.gradle.heroku.tasks.model.HerokuProcess
import com.heroku.api.http.Http

class SetFormationRequest extends BaseHerokuApiJsonRequest<Void> {

    private final List<HerokuProcess> processes
    private final String appName

    SetFormationRequest(String appName, List<HerokuProcess> processes) {
        this.processes = processes
        this.appName = appName
    }

    @Override
    Http.Method getHttpMethod() {
        Http.Method.PATCH
    }

    @Override
    String getEndpoint() {
        "/apps/$appName/formation"
    }

    @Override
    Map<String, Object> getBodyAsMap() {
        [updates: processes]
    }

}
