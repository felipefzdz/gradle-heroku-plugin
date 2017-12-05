package com.felipefzdz.gradle.heroku.heroku.api

import com.google.common.reflect.TypeToken
import com.heroku.api.exception.RequestFailedException
import com.heroku.api.http.Http
import com.heroku.api.http.HttpUtil
import com.heroku.api.request.Request
import groovy.json.JsonSlurper
import groovy.transform.CompileStatic

import java.nio.charset.StandardCharsets

@CompileStatic
abstract class BaseHerokuApiRequest<T> implements Request<T> {

    private final Class<T> responseType

    protected BaseHerokuApiRequest() {
        this.responseType = new TypeToken<T>(getClass()) {}.getRawType()
    }

    @Override
    Http.Method getHttpMethod() {
        Http.Method.GET
    }

    @Override
    boolean hasBody() {
        false
    }

    @Override
    String getBody() {
        throw HttpUtil.noBody()
    }

    @Override
    Map<String, ?> getBodyAsMap() {
        throw HttpUtil.noBody()
    }

    @Override
    Http.Accept getResponseType() {
        Http.Accept.JSON
    }

    @Override
    Map<String, String> getHeaders() {
        Collections.emptyMap()
    }

    protected Http.Status getExpectedResponseStatus() {
        httpMethod == Http.Method.POST ? Http.Status.CREATED : Http.Status.OK
    }

    @Override
    T getResponse(byte[] bytes, int status, Map<String, String> headers) {
        if (expectedResponseStatus.statusCode != status) {
            throw new RequestFailedException("$endpoint request failed", status, bytes)
        }

        if (responseType == Void) {
            null
        } else {
            def json = new JsonSlurper().parseText(new String(bytes, StandardCharsets.UTF_8))
            responseType.cast(json)
        }
    }
}
