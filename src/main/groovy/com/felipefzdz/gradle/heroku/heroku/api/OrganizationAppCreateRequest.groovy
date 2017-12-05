package com.felipefzdz.gradle.heroku.heroku.api

import com.heroku.api.Heroku

class OrganizationAppCreateRequest extends BaseHerokuApiJsonRequest<Map<String, ?>> {

    private final String appName
    private final String organizationName
    private final Heroku.Stack stack
    private final boolean personal

    OrganizationAppCreateRequest(String appName, String organizationName, Heroku.Stack stack, boolean personal) {
        this.appName = appName
        this.organizationName = organizationName
        this.stack = stack
        this.personal = personal
    }

    @Override
    String getEndpoint() {
        "/organizations/apps"
    }

    @Override
    Map<String, Object> getBodyAsMap() {
        [name: appName, organization: organizationName, personal: personal, stack: stack.value]
    }

}
