package com.felipefzdz.gradle.heroku.heroku.api

import com.heroku.api.http.Http
import groovy.json.JsonOutput
import groovy.transform.CompileStatic

@CompileStatic
abstract class BaseHerokuApiJsonRequest<T> extends BaseHerokuApiRequest<T> {

    @Override
    Http.Method getHttpMethod() {
        Http.Method.POST
    }

    @Override
    boolean hasBody() {
        true
    }

    @Override
    String getBody() {
        JsonOutput.toJson(getBodyAsMap())
    }

}
