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
    NamedDomainObjectContainer<HerokuAddon> addons
    NamedDomainObjectContainer<HerokuAddonAttachment> addonAttachments
    HerokuProcess herokuProcess
    List<String> domains
    ReadinessProbe readinessProbe

    HerokuApp(String name,
              NamedDomainObjectContainer<HerokuAddon> addons,
              NamedDomainObjectContainer<HerokuAddonAttachment> addonAttachments
    ) {
        this.name = name
        this.addons = addons
        this.addonAttachments = addonAttachments
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

    void process(Action<? super HerokuProcess> process) {
        this.herokuProcess = new HerokuProcess()
        process.execute(herokuProcess)
    }

    void addons(Action<? super NamedDomainObjectContainer<HerokuAddon>> action) {
        action.execute(addons)
    }

    void addonAttachments(Action<? super NamedDomainObjectContainer<HerokuAddonAttachment>> action) {
        action.execute(addonAttachments)
    }

    HerokuApp domains(List<String> domains) {
        this.domains = domains
        this
    }

    void readinessProbe(Action<? super ReadinessProbe> readinessProbe) {
        this.readinessProbe = new ReadinessProbe()
        readinessProbe.execute(this.readinessProbe)
    }

    String getTeamName() {
        return teamName ?: ''
    }

    String getStack() {
        return stack ?: 'heroku-16'
    }
}
