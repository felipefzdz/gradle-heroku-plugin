package com.felipefzdz.gradle.heroku.heroku

import com.felipefzdz.gradle.heroku.heroku.api.OrganizationAppCreateRequest
import com.heroku.api.Heroku
import com.heroku.api.HerokuAPI
import com.heroku.api.request.Request
import groovy.transform.CompileStatic
import org.gradle.api.logging.Logger

@CompileStatic
class HerokuClient {

    Logger logger
    HerokuAPI herokuAPI
    private final String apiKey

    HerokuClient(Logger logger, String apiKey) {
        this.apiKey = apiKey
        this.logger = logger
        this.herokuAPI = new HerokuAPI(apiKey)
    }

    void destroyApp(String appName) {
        logger.quiet("Destroying application $appName")
        herokuAPI.destroyApp(appName)
    }

    void createOrganization(String appName, String teamName, boolean personalApp) {
        logger.quiet("Creating heroku app $appName for team $teamName")
        api3(new OrganizationAppCreateRequest(appName, teamName, Heroku.Stack.Cedar14, personalApp))
    }

    boolean appExists(String appName) {
        herokuAPI.appExists(appName)
    }

    private <T> T api3(Request<T> request) {
        herokuAPI.connection.execute(request, apiKey)
    }
}
