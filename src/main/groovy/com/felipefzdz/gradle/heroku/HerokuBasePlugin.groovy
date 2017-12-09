package com.felipefzdz.gradle.heroku

import com.felipefzdz.gradle.heroku.heroku.DefaultHerokuClient
import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.*
import com.felipefzdz.gradle.heroku.tasks.model.HerokuAddon
import com.felipefzdz.gradle.heroku.tasks.model.HerokuApp
import com.felipefzdz.gradle.heroku.tasks.model.HerokuWebApp
import com.felipefzdz.gradle.heroku.tasks.services.ConfigureLogDrainsService
import com.felipefzdz.gradle.heroku.tasks.services.InstallAddonsService
import groovy.transform.CompileStatic
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.reflect.Instantiator

import javax.inject.Inject

@CompileStatic
class HerokuBasePlugin implements Plugin<Project> {

    public static final String HEROKU_EXTENSION_NAME = 'heroku'

    private final Instantiator instantiator

    @Inject
    HerokuBasePlugin(Instantiator instantiator) {
        this.instantiator = instantiator
    }

    @Override
    void apply(Project project) {
        HerokuExtension extension = project.extensions.create(HEROKU_EXTENSION_NAME, HerokuExtension, project, createHerokuAppContainer(project))
        createBaseTasks(extension, project)
    }


    private static void createBaseTasks(HerokuExtension extension, Project project) {
        HerokuClient herokuClient = new DefaultHerokuClient()
        InstallAddonsService installAddonsService = new InstallAddonsService(herokuClient)
        ConfigureLogDrainsService configureLogDrainsService = new ConfigureLogDrainsService(herokuClient)

        extension.bundle.all { HerokuApp app ->
            project.tasks.create("herokuCreate${app.name.capitalize()}", CreateAppTask) { CreateAppTask task ->
                task.apiKey = extension.apiKey
                task.app = app
                task.herokuClient = herokuClient
            }

            project.tasks.create("herokuDestroy${app.name.capitalize()}", DestroyAppTask) { DestroyAppTask task ->
                task.apiKey = extension.apiKey
                task.app = app
                task.herokuClient = herokuClient
            }

            project.tasks.create("herokuConfigureLogDrainsFor${app.name.capitalize()}", ConfigureLogDrainsTask) { ConfigureLogDrainsTask task ->
                task.apiKey = extension.apiKey
                task.app = app
                task.herokuClient = herokuClient
                task.configureLogDrainsService = configureLogDrainsService
            }

            if (app instanceof HerokuWebApp) {
                project.tasks.create("herokuInstallAddonsFor${app.name.capitalize()}", InstallAddonsTask) { InstallAddonsTask task ->
                    task.apiKey = extension.apiKey
                    task.app = app as HerokuWebApp
                    task.herokuClient = herokuClient
                    task.installAddonsService = installAddonsService
                }
            }
        }

        project.tasks.create("herokuCreateBundle", CreateBundleTask) { CreateBundleTask task ->
            task.apiKey = extension.apiKey
            task.bundle = extension.bundle
            task.herokuClient = herokuClient
        }

        project.tasks.create("herokuDestroyBundle", DestroyBundleTask) { DestroyBundleTask task ->
            task.apiKey = extension.apiKey
            task.bundle = extension.bundle
            task.herokuClient = herokuClient
        }
    }

    HerokuAppContainer createHerokuAppContainer(Project project) {
        def container = instantiator.newInstance(HerokuAppContainer, instantiator)
        container.registerFactory(HerokuWebApp) { String name ->
            return instantiator.newInstance(HerokuWebApp, name, project.container(HerokuAddon))
        }
        return container

    }
}
