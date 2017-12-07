package com.felipefzdz.gradle.heroku.tasks.model

class HerokuAddon {
    final String name
    String plan
    Boolean waitUntilStarted

    HerokuAddon(String name) {
        this.name = name.toUpperCase()
    }
}
