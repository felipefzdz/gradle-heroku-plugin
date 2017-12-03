package com.felipefzdz.gradle.heroku.tasks;

import com.heroku.api.HerokuAPI;
import org.gradle.api.DefaultTask;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

public class Destroyer extends DefaultTask {
    private final Property<String> apiKey;
    private final Property<String> appName;
    private HerokuAPI herokuApi;

    public Destroyer() {
        this.apiKey = getProject().getObjects().property(String.class);
        this.appName = getProject().getObjects().property(String.class);
    }

    public void setApiKey(String apiKey) {
        this.apiKey.set(apiKey);
    }

    public void setApiKey(Provider<String> apiKey) {
        this.apiKey.set(apiKey);
    }
    
    @Input
    public String getApiKey() {
        return apiKey.get();
    }

    public void setAppName(String appName) {
        this.appName.set(appName);
    }

    public void setAppName(Provider<String> appName) {
        this.appName.set(appName);
    }

    @Input
    public String getAppName() {
        return appName.get();
    }

    @TaskAction
    public void herokuDestroy() {
        herokuApi = new HerokuAPI(getApiKey());
        getLogger().info(String.format("Destroying application %s", getAppName()));
        herokuApi.destroyApp(getAppName());
        getLogger().info(String.format("Successfully destroyed app %s", getAppName()));
    }

}
