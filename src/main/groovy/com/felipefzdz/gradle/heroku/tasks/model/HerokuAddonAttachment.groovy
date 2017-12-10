package com.felipefzdz.gradle.heroku.tasks.model

import groovy.transform.CompileStatic

@CompileStatic
class HerokuAddonAttachment {
    final String name
    String owningApp

    HerokuAddonAttachment(String name) {
        this.name = name.toUpperCase()
    }
}
