package com.felipefzdz.gradle.heroku.tasks.services

import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.model.HerokuAddon
import com.heroku.api.AddonChange
import groovy.transform.CompileStatic

import java.time.Duration

import static com.felipefzdz.gradle.heroku.utils.AsyncUtil.waitFor

@CompileStatic
class InstallAddonsService {

    private final Boolean SKIP_WAITS = Boolean.valueOf(System.getenv('GRADLE_HEROKU_PLUGIN_SKIP_WAITS'))

    HerokuClient herokuClient

    InstallAddonsService(HerokuClient herokuClient) {
        this.herokuClient = herokuClient
    }

    void installAddons(List<HerokuAddon> addons, String appName) {
        def existingAddons = herokuClient.getAddonAttachments(appName)
        Map<HerokuAddon, AddonChange> added = [:]
        addons.each { HerokuAddon addon ->
            def existing = existingAddons.find { it.name == addon.name }
            if (existing) {
                println "Addon $addon already exists as an addon attachment and won't be installed"
            } else {
                added[addon] = herokuClient.installAddon(appName, addon.plan)
                println "Successfully installed addon ${addon.name}"
            }
        }
        waitForAddonsIfAdded(appName, added.keySet())
    }

    private void waitForAddonsIfAdded(String appName, Set<HerokuAddon> addedAddons) {
        addedAddons.findAll { HerokuAddon addon ->
            addon.waitUntilStarted
        }.each { HerokuAddon addon ->
            def addonUrl = waitForAddonUrl(appName, "${addon.name.replace('-', '_')}_URL")
            waitForSocketAvailable(addonUrl.host, addonUrl.port)
        }
    }

    private URI waitForAddonUrl(String appName, String addonUrl) {
        println "Waiting for $addonUrl to be set on app $appName"
        waitFor(Duration.ofMinutes(10), Duration.ofSeconds(5), "$addonUrl to be set on app $appName", SKIP_WAITS) {
            return URI.create(herokuClient.listConfig(appName)[addonUrl])
        }
    }

    private void waitForSocketAvailable(String host, int port) {
        println "Waiting for connection on $host:$port"
        waitFor(Duration.ofMinutes(5), Duration.ofSeconds(5), "database to appear at $host:$port", SKIP_WAITS) {
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
