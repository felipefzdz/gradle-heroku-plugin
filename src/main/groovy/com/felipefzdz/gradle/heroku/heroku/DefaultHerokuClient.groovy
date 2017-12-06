package com.felipefzdz.gradle.heroku.heroku

import com.felipefzdz.gradle.heroku.heroku.api.GetAddonAttachmentsRequest
import com.felipefzdz.gradle.heroku.heroku.api.OrganizationAppCreateRequest
import com.heroku.api.Heroku
import com.heroku.api.HerokuAPI
import com.heroku.api.request.Request
import com.heroku.api.request.addon.AddonInstall
import groovy.transform.CompileStatic
import org.gradle.api.logging.Logger

@CompileStatic
class DefaultHerokuClient implements HerokuClient {

    private Logger logger
    private HerokuAPI herokuAPI
    private String apiKey

    DefaultHerokuClient(Logger logger) {
        this.logger = logger
    }

    @Override
    HerokuClient init(String apiKey) {
        this.apiKey = apiKey
        this.herokuAPI = new HerokuAPI(apiKey)
        this
    }

    @Override
    void destroyApp(String appName) {
        logger.quiet("Destroying application $appName")
        herokuAPI.destroyApp(appName)
    }

    @Override
    void createApp(String appName, String teamName, boolean personalApp) {
        logger.quiet("Creating heroku app $appName for team $teamName")
        api3(new OrganizationAppCreateRequest(appName, teamName, Heroku.Stack.Cedar14, personalApp))
    }

    @Override
    boolean appExists(String appName) {
        herokuAPI.appExists(appName)
    }

    @Override
    void installAddon(String appName, String plan) {
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
