package com.felipefzdz.gradle.heroku.heroku.api

import com.heroku.api.exception.RequestFailedException
import com.heroku.api.http.Http

class AppDisableAcmRequest extends BaseHerokuApiRequest<Map<String, ?>> {

    private final String appName

    AppDisableAcmRequest(String appName) {
        this.appName = appName
    }

    @Override
    Http.Method getHttpMethod() {
        Http.Method.DELETE
    }

    @Override
    String getEndpoint() {
        "/apps/$appName/acm"
    }

    @Override
    protected Set<Http.Status> getExpectedResponseStatus() {
        [Http.Status.OK, Http.Status.CREATED, Http.Status.UNPROCESSABLE_ENTITY]
    }

    @Override
    protected Map<String, ?> validateResponse(int status, Map<String, ?> response) {
        if (Http.Status.UNPROCESSABLE_ENTITY.statusCode == status && response.id != 'acm_not_enabled') {
            throw new RequestFailedException("$endpoint request failed", status, response.toString())
        }
        response
    }
}
