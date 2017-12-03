package com.felipefzdz.gradle.heroku;

import org.gradle.api.Project;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;

public class HerokuExtension {

    private final Property<String> apiKey;
    private final Property<String> appName;
    private final Property<String> teamName;
    private final Property<Boolean> personalApp;

    public HerokuExtension(Project project) {
        this.apiKey = project.getObjects().property(String.class);
        this.appName = project.getObjects().property(String.class);
        this.teamName = project.getObjects().property(String.class);
        this.personalApp = project.getObjects().property(Boolean.class);
    }

    public String getApiKey() {
        return apiKey.get();
    }

    public Provider<String> getApiKeyProvider() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey.set(apiKey);
    }

    public String getAppName() {
        return appName.get();
    }

    public Provider<String> getAppNameProvider() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName.set(appName);
    }

    public String getTeamName() {
        return teamName.get();
    }

    public Provider<String> getTeamNameProvider() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName.set(teamName);
    }

    public Boolean getPersonalApp() {
        return personalApp.get();
    }

    public Provider<Boolean> getPersonalAppProvider() {
        return personalApp;
    }

    public void setPersonalApp(Boolean personalApp) {
        this.personalApp.set(personalApp);
    }

}
