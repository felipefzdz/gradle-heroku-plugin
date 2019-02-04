package com.felipefzdz.gradle.heroku

import com.felipefzdz.gradle.heroku.dependencyinjection.Graph
import com.felipefzdz.gradle.heroku.tasks.*
import com.felipefzdz.gradle.heroku.tasks.model.*
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.execution.TaskExecutionGraph
import org.gradle.internal.reflect.Instantiator
import org.gradle.api.internal.CollectionCallbackActionDecorator
import org.gradle.util.GUtil
import org.gradle.util.GradleVersion

import javax.inject.Inject

import static com.felipefzdz.gradle.heroku.dependencyinjection.Graph.*

class HerokuBasePlugin implements Plugin<Project> {

    public static final String HEROKU_EXTENSION_NAME = 'heroku'

    @Override
    void apply(Project project) {
        Graph.init(project.logger)
        Instantiator instantiator = project.services.get(Instantiator)
        NamedDomainObjectContainer<HerokuAppContainer> bundles = project.container(HerokuAppContainer) { String name ->
            createHerokuAppContainer(project, instantiator, name)
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

    @Inject
    protected CollectionCallbackActionDecorator getCollectionCallbackActionDecorator() {
        throw new UnsupportedOperationException();
    }

    private static void createBaseTasks(NamedDomainObjectContainer<HerokuAppContainer> bundles, Project project) {
        bundles.all { HerokuAppContainer bundle ->
            String bundleName = GUtil.toCamelCase(bundle.name)
            createBundleTasks(bundle, project, bundleName)
        }
    }

    private static void createBundleTasks(HerokuAppContainer bundle, Project project, String bundleName = '') {
        bundle.all { HerokuApp app ->
            def herokuAppTasks = []
            herokuAppTasks << project.tasks.create("herokuCreate$bundleName${GUtil.toCamelCase(app.name)}", CreateAppTask, herokuClient, createAppService)
            herokuAppTasks << project.tasks.create("herokuDestroy$bundleName${GUtil.toCamelCase(app.name)}", DestroyAppTask, herokuClient, destroyAppService)
            herokuAppTasks << project.tasks.create("herokuConfigureLogDrainsFor$bundleName${GUtil.toCamelCase(app.name)}", ConfigureLogDrainsTask, herokuClient, configureLogDrainsService)
            herokuAppTasks << project.tasks.create("herokuCreateBuildFor$bundleName${GUtil.toCamelCase(app.name)}", CreateBuildTask, herokuClient, createBuildService)
            herokuAppTasks << project.tasks.create("herokuAddEnvironmentConfigFor$bundleName${GUtil.toCamelCase(app.name)}", AddEnvironmentConfigTask, herokuClient, project.logger)
            herokuAppTasks << project.tasks.create("herokuInstallAddonsFor$bundleName${GUtil.toCamelCase(app.name)}", InstallAddonsTask, installAddonsService)
            herokuAppTasks.each { task ->
                task.group = 'deployment'
                task.app = app
            }

            if (app instanceof HerokuWebApp) {
                def herokuWebAppTasks = []
                herokuWebAppTasks << project.tasks.create("herokuEnableFeaturesFor$bundleName${GUtil.toCamelCase(app.name)}", EnableFeaturesTask, enableFeaturesService)
                herokuWebAppTasks << project.tasks.create("herokuAddAddonAttachmentsFor$bundleName${GUtil.toCamelCase(app.name)}", AddAddonAttachmentsTask, addAddonAttachmentsService)
                herokuWebAppTasks.each { task ->
                    task.group = 'deployment'
                    task.app = app as HerokuWebApp
                }
            }
        }
        def herokuBundleTasks = []
        herokuBundleTasks << project.tasks.create("herokuCreate${bundleName}Bundle", CreateBundleTask, herokuClient, createAppService)
        herokuBundleTasks << project.tasks.create("herokuDestroy${bundleName}Bundle", DestroyBundleTask, herokuClient, destroyAppService)
        herokuBundleTasks.each { task ->
            task.group = 'deployment'
            task.bundle = bundle
        }
    }

    HerokuAppContainer createHerokuAppContainer(Project project, Instantiator instantiator, String bundleName) {
        def container
        if(GradleVersion.current().compareTo(GradleVersion.version('5.1')) >= 0) {
            CollectionCallbackActionDecorator decorator = project.services.get(CollectionCallbackActionDecorator)
            container = instantiator.newInstance(HerokuAppContainer, bundleName, instantiator, decorator)
        } else {
            container = instantiator.newInstance(HerokuAppContainer, bundleName, instantiator)
        }

        container.registerFactory(HerokuWebApp) { String name ->
            return instantiator.newInstance(HerokuWebApp, name, deployWebService, project.container(HerokuAddon), project.container(HerokuAddonAttachment))
        }

        container.registerFactory(HerokuDatabaseApp) { String name ->
            return instantiator.newInstance(HerokuDatabaseApp, name, deployDatabaseService, project.container(HerokuAddon))
        }
        return container

    }
}
