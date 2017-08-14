package com.felipefzdz.gradle.heroku;

import com.felipefzdz.gradle.heroku.tasks.UrlVerify;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class HerokuPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        UrlVerifierExtension extension = project.getExtensions().create("verification", UrlVerifierExtension.class, project);
        UrlVerify verifyUrlTask = project.getTasks().create("verifyUrl", UrlVerify.class);
        verifyUrlTask.setUrl(extension.getUrlProvider());
    }
}
