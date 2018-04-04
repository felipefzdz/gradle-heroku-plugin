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
    NamedDomainObjectContainer<HerokuAddon> addons
    HerokuProcess herokuProcess
    Boolean disableAcm

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
