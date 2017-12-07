package com.felipefzdz.gradle.heroku.heroku.api

class OrganizationAppCreateRequest extends BaseHerokuApiJsonRequest<Map<String, ?>> {

    private final String appName
    private final String organizationName
    private final String stack
    private final boolean personal

    OrganizationAppCreateRequest(String appName, String organizationName, String stack, boolean personal) {
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
        [name: appName, organization: organizationName, personal: personal, stack: stack]
    }

}
