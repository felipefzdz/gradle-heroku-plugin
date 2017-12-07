package com.felipefzdz.gradle.heroku

import com.felipefzdz.gradle.heroku.tasks.model.HerokuAddon
import com.felipefzdz.gradle.heroku.tasks.model.HerokuApp
import groovy.transform.CompileStatic
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.provider.Property

@CompileStatic
class HerokuExtension {

    Property<String> apiKey
    Property<String> appName
    Property<String> teamName
    Property<String> stack
    Property<Boolean> personalApp
    Property<Boolean> recreate
    Collection<HerokuApp> bundle = new ArrayList<>()
    Project project

    HerokuExtension(Project project) {
        this.apiKey = project.objects.property(String)
        this.appName = project.objects.property(String)
        this.teamName = project.objects.property(String)
        this.stack = project.objects.property(String)
        this.personalApp = project.objects.property(Boolean)
        this.recreate = project.objects.property(Boolean)
        this.project = project
    }


    HerokuApp app(Closure closure) {
        HerokuApp app = project.configure(new HerokuApp(project), closure) as HerokuApp
        app.addons = project.extensions.getByName('addons') as NamedDomainObjectContainer<HerokuAddon>
        bundle << app
        app
    }
}
