package com.felipefzdz.gradle.heroku.heroku

import com.heroku.api.HerokuAPI

class HerokuAPIFactory {

    private static HerokuAPI INSTANCE

    static HerokuAPI create(String apiKey) {
        if (!INSTANCE) {
            INSTANCE = new HerokuAPI(apiKey)
        }
        INSTANCE
    }
}
