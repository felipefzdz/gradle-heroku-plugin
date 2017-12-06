package com.felipefzdz.gradle.heroku.heroku.api

class GetAddonAttachmentsRequest extends BaseHerokuApiRequest<List<Map<String, ?>>> {

    private final String appName

    GetAddonAttachmentsRequest(String appName) {
        this.appName = appName
    }

    @Override
    String getEndpoint() {
        "/apps/$appName/addon-attachments"
    }

}
