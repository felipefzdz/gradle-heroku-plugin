package com.felipefzdz.gradle.heroku.tasks.services

import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.model.HerokuAddon
import com.heroku.api.AddonChange
import groovy.transform.CompileStatic
import org.gradle.api.logging.Logger

import java.time.Duration

import static com.felipefzdz.gradle.heroku.utils.AsyncUtil.waitFor

@CompileStatic
class InstallAddonsService {

    private final Boolean SKIP_WAITS = Boolean.valueOf(System.getenv('GRADLE_HEROKU_PLUGIN_SKIP_WAITS'))

    private final HerokuClient herokuClient
    private final Logger logger

    InstallAddonsService(HerokuClient herokuClient, Logger logger) {
        this.herokuClient = herokuClient
        this.logger = logger
    }

    void installAddons(List<HerokuAddon> addons, String appName) {
        def existingAddons = herokuClient.getAddonAttachments(appName)
        Map<HerokuAddon, AddonChange> added = [:]
        addons.each { HerokuAddon addon ->
            def existing = existingAddons.find { it.name == addon.name }
            if (existing) {
                logger.lifecycle "Addon $addon already exists as an addon attachment and won't be installed"
            } else {
                added[addon] = herokuClient.installAddon(appName, addon.plan, addon.config)
                logger.lifecycle "Successfully installed addon ${addon.name}"
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
        logger.lifecycle "Waiting for $addonUrl to be set on app $appName"
        waitFor(Duration.ofMinutes(10), Duration.ofSeconds(5), "$addonUrl to be set on app $appName", SKIP_WAITS) {
            def deployedAddonUrl = herokuClient.listConfig(appName)[addonUrl]
            assert deployedAddonUrl != null : "$addonUrl has not been set up yet on app $appName"
            return URI.create(deployedAddonUrl)
        }
    }

    private void waitForSocketAvailable(String host, int port) {
        logger.lifecycle "Waiting for connection on $host:$port"
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
