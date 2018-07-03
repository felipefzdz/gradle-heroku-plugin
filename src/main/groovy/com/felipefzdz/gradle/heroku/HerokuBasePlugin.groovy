package com.felipefzdz.gradle.heroku

import com.felipefzdz.gradle.heroku.dependencyinjection.Graph
import com.felipefzdz.gradle.heroku.tasks.*
import com.felipefzdz.gradle.heroku.tasks.model.*
import groovy.transform.CompileStatic
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.execution.TaskExecutionGraph
import org.gradle.internal.reflect.Instantiator
import org.gradle.util.GUtil

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
        Graph.init(project.logger)
        NamedDomainObjectContainer<HerokuAppContainer> bundles = project.container(HerokuAppContainer) { String name ->
            createHerokuAppContainer(project, name)
        }
        project.extensions.create(HEROKU_EXTENSION_NAME, HerokuExtension, bundles)
        createBaseTasks(bundles, project)
        project.gradle.taskGraph.whenReady { TaskExecutionGraph graph ->
            if (graph.allTasks.any { it instanceof HerokuBaseTask }) {
                String apiKey = System.getenv("GRADLE_HEROKU_PLUGIN_API_KEY") ?: project.property('herokuPluginApiKey')
                assert apiKey != null
                herokuClient.init(apiKey)
            }
        }
    }


    private static void createBaseTasks(NamedDomainObjectContainer<HerokuAppContainer> bundles, Project project) {
        bundles.all { HerokuAppContainer bundle ->
            String bundleName = GUtil.toCamelCase(bundle.name)
            createBundleTasks(bundle, project, bundleName)
        }
    }

    private static void createBundleTasks(HerokuAppContainer bundle, Project project, String bundleName = '') {
        bundle.all { HerokuApp app ->
            project.tasks.create("herokuCreate$bundleName${GUtil.toCamelCase(app.name)}", CreateAppTask) { CreateAppTask task ->
                task.group = 'deployment'
                task.app = app
                task.herokuClient = herokuClient
                task.createAppService = createAppService
                task
            }

            project.tasks.create("herokuDestroy$bundleName${GUtil.toCamelCase(app.name)}", DestroyAppTask) { DestroyAppTask task ->
                task.group = 'deployment'
                task.app = app
                task.herokuClient = herokuClient
                task.destroyAppService = destroyAppService
                task
            }

            project.tasks.create("herokuConfigureLogDrainsFor$bundleName${GUtil.toCamelCase(app.name)}", ConfigureLogDrainsTask) { ConfigureLogDrainsTask task ->
                task.group = 'deployment'
                task.app = app
                task.herokuClient = herokuClient
                task.configureLogDrainsService = configureLogDrainsService
                task
            }

            project.tasks.create("herokuCreateBuildFor$bundleName${GUtil.toCamelCase(app.name)}", CreateBuildTask) { CreateBuildTask task ->
                task.group = 'deployment'
                task.app = app
                task.herokuClient = herokuClient
                task.createBuildService = createBuildService
                task
            }

            project.tasks.create("herokuAddEnvironmentConfigFor$bundleName${GUtil.toCamelCase(app.name)}", AddEnvironmentConfigTask) { AddEnvironmentConfigTask task ->
                task.group = 'deployment'
                task.app = app
                task.herokuClient = herokuClient
                task.logger = project.logger
                task
            }

            project.tasks.create("herokuInstallAddonsFor$bundleName${GUtil.toCamelCase(app.name)}", InstallAddonsTask) { InstallAddonsTask task ->
                task.group = 'deployment'
                task.app = app
                task.installAddonsService = installAddonsService
                task
            }

            if (app instanceof HerokuWebApp) {
                project.tasks.create("herokuEnableFeaturesFor$bundleName${GUtil.toCamelCase(app.name)}", EnableFeaturesTask) { EnableFeaturesTask task ->
                    task.group = 'deployment'
                    task.app = app as HerokuWebApp
                    task.enableFeaturesService = enableFeaturesService
                    task
                }
                project.tasks.create("herokuAddAddonAttachmentsFor$bundleName${GUtil.toCamelCase(app.name)}", AddAddonAttachmentsTask) { AddAddonAttachmentsTask task ->
                    task.group = 'deployment'
                    task.app = app as HerokuWebApp
                    task.addAddonAttachmentsService = addAddonAttachmentsService
                    task
                }
            }
        }
        project.tasks.create("herokuCreate${bundleName}Bundle", CreateBundleTask) { CreateBundleTask task ->
            task.group = 'deployment'
            task.bundle = bundle
            task.herokuClient = herokuClient
            task.createAppService = createAppService
            task
        }

        project.tasks.create("herokuDestroy${bundleName}Bundle", DestroyBundleTask) { DestroyBundleTask task ->
            task.group = 'deployment'
            task.bundle = bundle
            task.herokuClient = herokuClient
            task.destroyAppService = destroyAppService
            task
        }
    }

    HerokuAppContainer createHerokuAppContainer(Project project, String bundleName) {
        def container = instantiator.newInstance(HerokuAppContainer, bundleName, instantiator)
        container.registerFactory(HerokuWebApp) { String name ->
            return instantiator.newInstance(HerokuWebApp, name, deployWebService, project.container(HerokuAddon), project.container(HerokuAddonAttachment))
        }

        container.registerFactory(HerokuDatabaseApp) { String name ->
            return instantiator.newInstance(HerokuDatabaseApp, name, deployDatabaseService, project.container(HerokuAddon))
        }
        return container

    }
}
