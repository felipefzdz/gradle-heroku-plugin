package com.felipefzdz.gradle.heroku

import groovy.transform.CompileStatic
import org.gradle.api.Project
import org.gradle.api.provider.Property

@CompileStatic
class HerokuExtension {

    Property<String> apiKey
    Property<String> appName
    Property<String> teamName
    Property<Boolean> personalApp
    Property<Boolean> recreate

    HerokuExtension(Project project) {
        this.apiKey = project.objects.property(String.class)
        this.appName = project.objects.property(String.class)
        this.teamName = project.objects.property(String.class)
        this.personalApp = project.objects.property(Boolean.class)
        this.recreate = project.objects.property(Boolean.class)
    }
}
