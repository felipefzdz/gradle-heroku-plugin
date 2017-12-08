package com.felipefzdz.gradle.heroku

import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.provider.Property

@CompileStatic
class HerokuExtension {

    Property<String> apiKey
    HerokuAppContainer bundle

    HerokuExtension(Project project, HerokuAppContainer bundle) {
        this.apiKey = project.objects.property(String)
        this.bundle = bundle
    }

    void bundle(Action<? super HerokuAppContainer> action) {
        action.execute(bundle)
    }

}
