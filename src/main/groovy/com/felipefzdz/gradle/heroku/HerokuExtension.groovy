package com.felipefzdz.gradle.heroku

import org.gradle.api.Project
import org.gradle.api.provider.Property

class HerokuExtension {

    Property<String> apiKey
    Property<String> appName
    Property<String> teamName
    Property<Boolean> personalApp

    HerokuExtension(Project project) {
        this.apiKey = project.objects.property(String.class)
        this.appName = project.objects.property(String.class)
        this.teamName = project.objects.property(String.class)
        this.personalApp = project.objects.property(Boolean.class)
    }
}
