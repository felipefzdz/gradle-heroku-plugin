package com.felipefzdz.gradle.heroku.tasks.services

import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.model.HerokuAddon
import com.felipefzdz.gradle.heroku.tasks.model.HerokuAddonAttachment
import com.heroku.api.AddonChange
import groovy.transform.CompileStatic

import java.time.Duration

import static com.felipefzdz.gradle.heroku.utils.AsyncUtil.waitFor

@CompileStatic
class AddAddonAttachmentsService {

    HerokuClient herokuClient

    AddAddonAttachmentsService(HerokuClient herokuClient) {
        this.herokuClient = herokuClient
    }

    void addAddonAttachments(List<HerokuAddonAttachment> addonAttachments, String apiKey, String appName) {
        herokuClient.init(apiKey)
        addonAttachments.each { HerokuAddonAttachment attachment ->
            println "Adding addon attachment ${attachment}"
            def existing = findAddonAttachmentByName(appName, attachment.name)
            if (!existing) {
                def addonResponse = findAddonAttachmentByName(attachment.owningApp, attachment.name)
                assert addonResponse: "Could not find an addon named $attachment.name belonging to the application $attachment.owningApp"
                herokuClient.createAddonAttachment(appName, addonResponse['addon']['id'] as String, attachment.name)
                println "Successfully added addon attachment $attachment.name"
            } else {
                println("App: $appName already has an addon attachment named: $attachment.name, skipping")
            }
        }
    }

    private Map<String,?> findAddonAttachmentByName(String appName, String addonName) {
        println "Getting addon attachments for ${appName}"

        def addons = herokuClient.getAddonAttachments(appName)
        addons.find { it.name == addonName }
    }

}