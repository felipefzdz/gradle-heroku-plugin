package com.felipefzdz.gradle.heroku.tasks.model

import groovy.transform.CompileStatic

@CompileStatic
class HerokuAddon {
    final String name
    String plan = ''
    Boolean waitUntilStarted = false
    Map<String, String> config = [:]

    HerokuAddon(String name) {
        this.name = name.toUpperCase()
    }

    @Override
    String toString() {
        "HerokuAddon{" +
            "name='" + name + '\'' +
            ", plan='" + plan + '\'' +
            ", waitUntilStarted=" + waitUntilStarted +
            ", config=" + config +
            '}'
    }
}
