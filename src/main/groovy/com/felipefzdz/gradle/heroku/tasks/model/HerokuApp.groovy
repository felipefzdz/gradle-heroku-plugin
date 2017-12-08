package com.felipefzdz.gradle.heroku.tasks.model

import groovy.transform.CompileStatic
import org.gradle.api.Named

@CompileStatic
class HerokuApp implements Named {

    String name
    String teamName
    String stack
    Boolean personalApp
    Boolean recreate
    List<String> logDrains

    HerokuApp(String name) {
        this.name = name
    }

    HerokuApp teamName(String teamName) {
        this.teamName = teamName
        this
    }

    HerokuApp stack(String stack) {
        this.stack = stack
        this
    }

    HerokuApp personalApp(Boolean personalApp) {
        this.personalApp = personalApp
        this
    }

    HerokuApp recreate(Boolean recreate) {
        this.recreate = recreate
        this
    }

    HerokuApp logDrains(List<String> logDrains) {
        this.logDrains = logDrains
        this
    }

    String getTeamName() {
        return teamName ?: ''
    }

    String getStack() {
        return stack ?: 'heroku-16'
    }
}
