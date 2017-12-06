package com.felipefzdz.gradle.heroku.tasks.model

class HerokuAddon {
    String plan
    final String name

    HerokuAddon(String name) {
        this.name = name.toUpperCase()
    }
}
