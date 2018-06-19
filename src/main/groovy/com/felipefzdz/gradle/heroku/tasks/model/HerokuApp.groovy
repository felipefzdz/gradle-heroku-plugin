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
    Boolean exclude = false
    Boolean personalApp = false
    Boolean recreate = false
    List<String> logDrains
    BuildSource buildSource
    HerokuConfig herokuConfig
    NamedDomainObjectContainer<HerokuAddon> addons
    HerokuProcess herokuProcess
    Boolean disableAcm = false

    HerokuApp(String name, NamedDomainObjectContainer<HerokuAddon> addons) {
        this.name = name
        this.addons = addons
    }

    HerokuApp teamName(String teamName) {
        this.teamName = teamName
        this
    }

    HerokuApp stack(String stack) {
        this.stack = stack
        this
    }

    HerokuApp exclude(Boolean exclude) {
        this.exclude = exclude
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

    void buildSource(Action<? super BuildSource> source) {
        this.buildSource = new BuildSource()
        source.execute(buildSource)
    }

    HerokuApp buildSource(BuildSource build) {
        this.buildSource = build
        this
    }

    void config(Action<? super HerokuConfig> config) {
        this.herokuConfig = new HerokuConfig()
        config.execute(herokuConfig)
    }

    void process(Action<? super HerokuProcess> process) {
        this.herokuProcess = new HerokuProcess()
        process.execute(herokuProcess)
    }

    void addons(Action<? super NamedDomainObjectContainer<HerokuAddon>> action) {
        action.execute(addons)
    }

    HerokuApp disableAcm(Boolean disableAcm) {
        this.disableAcm = disableAcm
        this
    }

    String getTeamName() {
        return teamName ?: ''
    }

    String getStack() {
        return stack ?: 'heroku-16'
    }

    void deploy(int delayAfterDestroyApp) {
        throw new UnsupportedOperationException()
    }
}
