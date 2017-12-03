package com.felipefzdz.gradle.heroku;

import com.felipefzdz.gradle.heroku.tasks.Deployer;
import com.felipefzdz.gradle.heroku.tasks.Destroyer;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class HerokuPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        HerokuExtension extension = project.getExtensions().create("heroku", HerokuExtension.class, project);

        Deployer deployerTask = project.getTasks().create("herokuDeploy", Deployer.class);
        deployerTask.setApiKey(extension.getApiKeyProvider());
        deployerTask.setAppName(extension.getAppNameProvider());
        deployerTask.setTeamName(extension.getTeamNameProvider());
        deployerTask.setPersonalApp(extension.getPersonalAppProvider());

        Destroyer destroyerTask = project.getTasks().create("herokuDestroyApp", Destroyer.class);
        destroyerTask.setApiKey(extension.getApiKeyProvider());
        destroyerTask.setAppName(extension.getAppNameProvider());
    }
}
