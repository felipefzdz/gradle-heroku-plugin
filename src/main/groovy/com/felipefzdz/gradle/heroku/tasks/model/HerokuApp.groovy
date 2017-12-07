package com.felipefzdz.gradle.heroku.tasks.model

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project

class HerokuApp {
    String name
    String teamName
    String stack
    Boolean personalApp
    NamedDomainObjectContainer<HerokuAddon> addons

    HerokuApp(Project project) {
        this.addons = project.container(HerokuAddon)
    }

    void setAddons(List<HerokuAddon> addons) {
        addons.each { HerokuAddon addon ->
            this.addons.create(addon.name, { HerokuAddon it ->
                it.plan = addon.plan
                it.waitUntilStarted = addon.waitUntilStarted
            })
        }
    }

    void setAddons(NamedDomainObjectContainer<HerokuAddon> addons) {
        this.addons = addons
    }

    String getTeamName() {
        return teamName ?: ''
    }

    String getStack() {
        return stack ?: 'heroku-16'
    }
}
