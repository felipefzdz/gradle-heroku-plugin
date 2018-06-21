package com.felipefzdz.gradle.heroku.tasks.model

import groovy.transform.CompileStatic
import groovy.transform.ToString

@CompileStatic
@ToString
class HerokuAddonAttachment {
    final String name
    String owningApp = ''

    HerokuAddonAttachment(String name) {
        this.name = name.toUpperCase()
    }

    @Override
    String toString() {
        return "HerokuAddonAttachment{" +
                "name='" + name + '\'' +
                ", owningApp='" + owningApp + '\'' +
                '}';
    }
}
