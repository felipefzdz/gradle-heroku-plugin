package com.felipefzdz.gradle.heroku.tasks

import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.model.HerokuAddon
import com.heroku.api.AddonChange
import groovy.transform.CompileStatic

import java.time.Duration

import static com.felipefzdz.gradle.heroku.utils.AsyncUtil.waitFor

@CompileStatic
class InstallAddonsService {

    HerokuClient herokuClient

    InstallAddonsService(HerokuClient herokuClient) {
        this.herokuClient = herokuClient
    }

    void installAddons(List<HerokuAddon> addons, String apiKey, String appName) {
        herokuClient.init(apiKey)
        def existingAddons = herokuClient.getAddonAttachments(appName)
        Map<HerokuAddon, AddonChange> added = [:]
        addons.each { HerokuAddon addon ->
            def existing = existingAddons.find { it.name == addon.name }
            if (existing) {
                println "Addon $addon already exists as an addon attachment and won't be installed"
            } else {
                println "Successfully installed addon ${addon.name}"
                added[addon] = herokuClient.installAddon(appName, addon.plan)
            }
        }
        waitForAddonsIfAdded(appName, added.keySet())
    }

    private void waitForAddonsIfAdded(String appName, Set<HerokuAddon> addedAddons) {
        addedAddons.findAll { HerokuAddon addon ->
            addon.waitUntilStarted
        }.each { HerokuAddon addon ->
            def addonUrl = waitForAddonUrl(appName, "${addon.name}_URL")
            waitForSocketAvailable(addonUrl.host, addonUrl.port)
        }
    }

    private URI waitForAddonUrl(String appName, String addonUrl) {
        println "Waiting for $addonUrl to be set on app $appName"
        waitFor(Duration.ofMinutes(5), Duration.ofSeconds(5), "$addonUrl to be set on app $appName") {
            return URI.create(herokuClient.listConfig(appName)[addonUrl])
        }
    }

    private void waitForSocketAvailable(String host, int port) {
        println "Waiting for connection on $host:$port"
        waitFor(Duration.ofMinutes(5), Duration.ofSeconds(5), "database to appear at $host:$port") {
            tryConnect(host, port)
        }
    }

    private void tryConnect(String host, int port) throws IOException {
        Socket s
        try {
            s = new Socket(host, port)
            true
        } finally {
            s?.close()
        }
    }
}
