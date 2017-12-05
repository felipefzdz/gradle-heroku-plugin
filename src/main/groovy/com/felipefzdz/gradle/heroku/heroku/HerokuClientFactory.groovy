package com.felipefzdz.gradle.heroku.heroku

import org.gradle.api.logging.Logger

class HerokuClientFactory {

    private static HerokuClient INSTANCE

    static HerokuClient create(Logger logger, String apiKey) {
        if (!INSTANCE) {
            INSTANCE = new HerokuClient(logger, apiKey)
        }
        INSTANCE
    }
}
