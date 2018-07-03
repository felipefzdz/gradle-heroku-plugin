package com.felipefzdz.gradle.heroku.tasks.services

import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.model.HerokuAddonAttachment
import groovy.transform.CompileStatic
import org.gradle.api.logging.Logger

@CompileStatic
class AddAddonAttachmentsService {

    HerokuClient herokuClient

    Logger logger

    AddAddonAttachmentsService(HerokuClient herokuClient, Logger logger) {
        this.herokuClient = herokuClient
        this.logger = logger
    }

    void addAddonAttachments(List<HerokuAddonAttachment> addonAttachments, String appName) {
        addonAttachments.each { HerokuAddonAttachment attachment ->
            logger.lifecycle "Adding addon attachment ${attachment}"
            def existing = findAddonAttachmentByName(appName, attachment.name)
            if (!existing) {
                def addonResponse = findAddonAttachmentByName(attachment.owningApp, attachment.name)
                assert addonResponse: "Could not find an addon named $attachment.name belonging to the application $attachment.owningApp"
                herokuClient.createAddonAttachment(appName, addonResponse['addon']['id'] as String, attachment.name)
                logger.lifecycle "Successfully added addon attachment $attachment.name"
            } else {
                logger.lifecycle("App: $appName already has an addon attachment named: $attachment.name, skipping")
            }
        }
    }

    private Map<String, ?> findAddonAttachmentByName(String appName, String addonName) {
        logger.lifecycle "Getting addon attachments for ${appName}"

        def addons = herokuClient.getAddonAttachments(appName)
        addons.find { it.name == addonName }
    }

}
