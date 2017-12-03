package com.felipefzdz.gradle.heroku.heroku.api;

import com.heroku.api.http.Http;
import groovy.json.JsonOutput;

public abstract class BaseHerokuApiJsonRequest<T> extends BaseHerokuApiRequest<T> {

    @Override
    public Http.Method getHttpMethod() {
        return Http.Method.POST;
    }

    @Override
    public boolean hasBody() {
        return true;
    }

    @Override
    public String getBody() {
        return JsonOutput.toJson(getBodyAsMap());
    }

}
