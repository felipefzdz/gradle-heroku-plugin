package com.felipefzdz.gradle.heroku.heroku

import com.felipefzdz.gradle.heroku.heroku.api.GetAddonAttachmentsRequest
import com.felipefzdz.gradle.heroku.heroku.api.OrganizationAppCreateRequest
import com.heroku.api.AddonChange
import com.heroku.api.Heroku
import com.heroku.api.HerokuAPI
import com.heroku.api.request.Request
import com.heroku.api.request.addon.AddonInstall
import groovy.transform.CompileStatic

@CompileStatic
class DefaultHerokuClient implements HerokuClient {

    private HerokuAPI herokuAPI
    private String apiKey

    @Override
    HerokuClient init(String apiKey) {
        if (herokuAPI == null) {
            this.apiKey = apiKey
            this.herokuAPI = new HerokuAPI(apiKey)
        }
        this
    }

    @Override
    void destroyApp(String appName) {
        println "Destroying application $appName"
        herokuAPI.destroyApp(appName)
    }

    @Override
    void createApp(String appName, String teamName, boolean personalApp, String stack) {
        println "Creating heroku app $appName for team $teamName"
        api3(new OrganizationAppCreateRequest(appName, teamName, stack, personalApp))
    }

    @Override
    boolean appExists(String appName) {
        herokuAPI.appExists(appName)
    }

    @Override
    AddonChange installAddon(String appName, String plan) {
        api3(new AddonInstall(appName, plan))
    }

    @Override
    List<Map<String, ?>> getAddonAttachments(String appName) {
        api3(new GetAddonAttachmentsRequest(appName))
    }

    private <T> T api3(Request<T> request) {
        herokuAPI.connection.execute(request, apiKey)
    }
}
