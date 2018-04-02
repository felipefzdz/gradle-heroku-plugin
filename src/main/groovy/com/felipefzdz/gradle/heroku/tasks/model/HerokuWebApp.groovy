package com.felipefzdz.gradle.heroku.tasks.model

import com.felipefzdz.gradle.heroku.tasks.services.DeployWebService
import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer

@CompileStatic
class HerokuWebApp extends HerokuApp {

    NamedDomainObjectContainer<HerokuAddonAttachment> addonAttachments
    List<String> features
    List<String> domains
    ReadinessProbe readinessProbe

    DeployWebService deployWebService

    HerokuWebApp(String name, DeployWebService deployWebService, NamedDomainObjectContainer<HerokuAddon> addons, NamedDomainObjectContainer<HerokuAddonAttachment> addonAttachments) {
        super(name, addons)
        this.addonAttachments = addonAttachments
        this.deployWebService = deployWebService
    }

    @Override
    void deploy(int delayAfterDestroyApp, String apiKey) {
        deployWebService.deploy(this, delayAfterDestroyApp, apiKey)
    }

    void addonAttachments(Action<? super NamedDomainObjectContainer<HerokuAddonAttachment>> action) {
        action.execute(addonAttachments)
    }

    HerokuWebApp features(List<String> features) {
        this.features = features
        this
    }

    HerokuWebApp domains(List<String> domains) {
        this.domains = domains
        this
    }

    void readinessProbe(Action<? super ReadinessProbe> readinessProbe) {
        this.readinessProbe = new ReadinessProbe()
        readinessProbe.execute(this.readinessProbe)
    }

}
