package com.felipefzdz.gradle.heroku.tasks.model

import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer

@CompileStatic
class HerokuWebApp extends HerokuApp {

    NamedDomainObjectContainer<HerokuAddon> addons

    HerokuWebApp(String name, NamedDomainObjectContainer<HerokuAddon> addons) {
        super(name)
        this.addons = addons
    }

    void addons(Action<? super NamedDomainObjectContainer<HerokuAddon>> action) {
        action.execute(addons)
    }
}
