package com.felipefzdz.gradle.heroku.tasks

import com.felipefzdz.gradle.heroku.tasks.model.HerokuWebApp
import com.felipefzdz.gradle.heroku.tasks.services.AddAddonAttachmentsService
import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

import javax.inject.Inject

@CompileStatic
class AddAddonAttachmentsTask extends HerokuBaseTask {

    @Internal
    HerokuWebApp app

    private final AddAddonAttachmentsService addAddonAttachmentsService

    @Inject
    AddAddonAttachmentsTask(AddAddonAttachmentsService addAddonAttachmentsService) {
        this.addAddonAttachmentsService = addAddonAttachmentsService
    }

    @TaskAction
    void addAddonAttachments() {
        addAddonAttachmentsService.addAddonAttachments(app.addonAttachments.toList(), app.name)
    }
}
