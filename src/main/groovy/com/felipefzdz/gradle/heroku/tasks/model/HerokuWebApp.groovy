package com.felipefzdz.gradle.heroku.tasks.model

import groovy.transform.CompileStatic
import org.gradle.api.NamedDomainObjectContainer

@CompileStatic
class HerokuWebApp extends HerokuApp {

    HerokuWebApp(String name, NamedDomainObjectContainer<HerokuAddon> addons, NamedDomainObjectContainer<HerokuAddonAttachment> addonAttachments) {
        super(name, addons, addonAttachments)
    }
}
