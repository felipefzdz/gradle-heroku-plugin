package com.felipefzdz.gradle.heroku.tasks.model

import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.Named
import org.gradle.api.NamedDomainObjectContainer

@CompileStatic
class HerokuApp implements Named {

    String name
    String teamName
    String stack
    Boolean personalApp
    Boolean recreate
    List<String> logDrains
    BuildSource buildSource
    Map<String, String> config
    List<String> features

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

    void build(Action<? super BuildSource> source) {
        this.buildSource = new BuildSource()
        source.execute(buildSource)
    }

    HerokuApp build(BuildSource build) {
        this.buildSource = build
        this
    }

    HerokuApp config(Map<String, String> config) {
        this.config = config
        this
    }

    HerokuApp features(List<String> features) {
        this.features = features
        this
    }

    String getTeamName() {
        return teamName ?: ''
    }

    String getStack() {
        return stack ?: 'heroku-16'
    }
}
