package com.felipefzdz.gradle.heroku.heroku.api

class CreateAddonAttachmentRequest extends BaseHerokuApiJsonRequest<Void> {

    private final String appName
    private final String addonId
    private final String addonName

    CreateAddonAttachmentRequest(String appName, String addonId, String addonName) {
        this.appName = appName
        this.addonId = addonId
        this.addonName = addonName
    }

    @Override
    String getEndpoint() {
        "/addon-attachments"
    }

    @Override
    Map<String, Object> getBodyAsMap() {
        [addon: addonId, app: appName, name: addonName]
    }

}
