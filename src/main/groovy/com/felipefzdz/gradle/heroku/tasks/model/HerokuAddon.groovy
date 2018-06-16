package com.felipefzdz.gradle.heroku.tasks.model

import groovy.transform.CompileStatic

@CompileStatic
class HerokuAddon {
    final String name
    String plan
    Boolean waitUntilStarted

    HerokuAddon(String name) {
        this.name = name.toUpperCase()
    }

    @Override
    String toString() {
        return "HerokuAddon{" +
                "name='" + name + '\'' +
                ", plan='" + plan + '\'' +
                ", waitUntilStarted=" + waitUntilStarted +
                '}';
    }
}
