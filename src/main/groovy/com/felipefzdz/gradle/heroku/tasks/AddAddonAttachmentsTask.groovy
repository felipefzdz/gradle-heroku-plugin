package com.felipefzdz.gradle.heroku.tasks

import com.felipefzdz.gradle.heroku.tasks.model.HerokuWebApp
import com.felipefzdz.gradle.heroku.tasks.services.AddAddonAttachmentsService
import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

@CompileStatic
class AddAddonAttachmentsTask extends DefaultTask {

    @Internal
    HerokuWebApp app

    @Internal
    AddAddonAttachmentsService addAddonAttachmentsService


    @TaskAction
    void addAddonAttachments() {
        addAddonAttachmentsService.addAddonAttachments(app.addonAttachments.toList(), app.name)
    }
}
