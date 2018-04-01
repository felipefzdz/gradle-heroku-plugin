package com.felipefzdz.gradle.heroku

import com.felipefzdz.gradle.heroku.dependencyinjection.Graph
import com.felipefzdz.gradle.heroku.tasks.*
import com.felipefzdz.gradle.heroku.tasks.model.HerokuAddon
import com.felipefzdz.gradle.heroku.tasks.model.HerokuAddonAttachment
import com.felipefzdz.gradle.heroku.tasks.model.HerokuApp
import com.felipefzdz.gradle.heroku.tasks.model.HerokuWebApp
import groovy.transform.CompileStatic
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.reflect.Instantiator

import javax.inject.Inject

import static com.felipefzdz.gradle.heroku.dependencyinjection.Graph.*

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
        Graph.init()
        HerokuExtension extension = project.extensions.create(HEROKU_EXTENSION_NAME, HerokuExtension, project, createHerokuAppContainer(project))
        createBaseTasks(extension, project)
    }


    private static void createBaseTasks(HerokuExtension extension, Project project) {
        extension.bundle.all { HerokuApp app ->
            project.tasks.create("herokuCreate${app.name.capitalize()}", CreateAppTask) { CreateAppTask task ->
                task.apiKey = extension.apiKey
                task.app = app
                task.herokuClient = herokuClient
                task.createAppService = createAppService
            }

            project.tasks.create("herokuDestroy${app.name.capitalize()}", DestroyAppTask) { DestroyAppTask task ->
                task.apiKey = extension.apiKey
                task.app = app
                task.herokuClient = herokuClient
                task.destroyAppService = destroyAppService
            }

            project.tasks.create("herokuConfigureLogDrainsFor${app.name.capitalize()}", ConfigureLogDrainsTask) { ConfigureLogDrainsTask task ->
                task.apiKey = extension.apiKey
                task.app = app
                task.herokuClient = herokuClient
                task.configureLogDrainsService = configureLogDrainsService
            }

            project.tasks.create("herokuCreateBuildFor${app.name.capitalize()}", CreateBuildTask) { CreateBuildTask task ->
                task.apiKey = extension.apiKey
                task.app = app
                task.herokuClient = herokuClient
                task.createBuildService = createBuildService
            }

            project.tasks.create("herokuAddEnvironmentConfigFor${app.name.capitalize()}", AddEnvironmentConfigTask) { AddEnvironmentConfigTask task ->
                task.apiKey = extension.apiKey
                task.app = app
                task.herokuClient = herokuClient
            }

            project.tasks.create("herokuEnableFeaturesFor${app.name.capitalize()}", EnableFeaturesTask) { EnableFeaturesTask task ->
                task.apiKey = extension.apiKey
                task.app = app
                task.enableFeaturesService = enableFeaturesService
            }

            project.tasks.create("herokuInstallAddonsFor${app.name.capitalize()}", InstallAddonsTask) { InstallAddonsTask task ->
                task.apiKey = extension.apiKey
                task.app = app
                task.installAddonsService = installAddonsService
            }

            project.tasks.create("herokuAddAddonAttachmentsFor${app.name.capitalize()}", AddAddonAttachmentsTask) { AddAddonAttachmentsTask task ->
                task.apiKey = extension.apiKey
                task.app = app
                task.addAddonAttachmentsService = addAddonAttachmentsService
            }
        }

        project.tasks.create("herokuCreateBundle", CreateBundleTask) { CreateBundleTask task ->
            task.apiKey = extension.apiKey
            task.bundle = extension.bundle
            task.herokuClient = herokuClient
            task.createAppService = createAppService
        }

        project.tasks.create("herokuDestroyBundle", DestroyBundleTask) { DestroyBundleTask task ->
            task.apiKey = extension.apiKey
            task.bundle = extension.bundle
            task.herokuClient = herokuClient
            task.destroyAppService = destroyAppService
        }
    }

    HerokuAppContainer createHerokuAppContainer(Project project) {
        def container = instantiator.newInstance(HerokuAppContainer, instantiator)
        container.registerFactory(HerokuWebApp) { String name ->
            return instantiator.newInstance(HerokuWebApp, name, project.container(HerokuAddon), project.container(HerokuAddonAttachment))
        }
        return container

    }
}
