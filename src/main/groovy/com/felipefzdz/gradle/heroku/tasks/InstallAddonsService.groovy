package com.felipefzdz.gradle.heroku.tasks

import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.model.HerokuAddon
import groovy.transform.CompileStatic

@CompileStatic
class InstallAddonsService {

    HerokuClient herokuClient

    InstallAddonsService(HerokuClient herokuClient) {
        this.herokuClient = herokuClient
    }

    Map<HerokuAddon, Map<String, ?>> installAddons(List<HerokuAddon> addons, String apiKey, String appName) {
        herokuClient.init(apiKey)
        def existingAddons = herokuClient.getAddonAttachments(appName)
        def added = [:]
        addons.each { HerokuAddon addon ->
            def existing = existingAddons.find { it.name == addon.name }
            if (existing) {
                println "Addon $addon already exists as an addon attachment and won't be installed"
            } else {
                println "Successfully installed addon ${addon.name}"
                added[addon] = herokuClient.installAddon(appName, addon.plan)
            }
        }
        added
    }
}
