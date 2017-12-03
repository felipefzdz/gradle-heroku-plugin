package com.felipefzdz.gradle.heroku.heroku.api;

import com.google.common.reflect.TypeToken;
import com.heroku.api.exception.RequestFailedException;
import com.heroku.api.http.Http;
import com.heroku.api.http.HttpUtil;
import com.heroku.api.request.Request;
import groovy.json.JsonSlurper;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

public abstract class BaseHerokuApiRequest<T> implements Request<T> {

    private final Class<? super T> responseType;

    protected BaseHerokuApiRequest() {
        this.responseType = new TypeToken<T>(BaseHerokuApiRequest.this.getClass()) {}.getRawType();
    }

    @Override
    public Http.Method getHttpMethod() {
        return Http.Method.GET;
    }

    @Override
    public boolean hasBody() {
        return false;
    }

    @Override
    public String getBody() {
        throw HttpUtil.noBody();
    }

    @Override
    public Map<String, ?> getBodyAsMap() {
        throw HttpUtil.noBody();
    }

    @Override
    public Http.Accept getResponseType() {
        return Http.Accept.JSON;
    }

    @Override
    public Map<String, String> getHeaders() {
        return Collections.emptyMap();
    }

    protected Http.Status getExpectedResponseStatus() {
        return getHttpMethod().equals(Http.Method.POST) ? Http.Status.CREATED : Http.Status.OK;
    }

    @Override
    public T getResponse(byte[] bytes, int status, Map<String, String> headers) {
        if (getExpectedResponseStatus().statusCode != status) {
            throw new RequestFailedException(getEndpoint() + " request failed", status, bytes);
        }

        if (responseType.equals(Void.class)) {
            return null;
        } else {
            Object json = new JsonSlurper().parseText(new String(bytes, StandardCharsets.UTF_8));
            return (T) responseType.cast(json);
        }

    }

}
