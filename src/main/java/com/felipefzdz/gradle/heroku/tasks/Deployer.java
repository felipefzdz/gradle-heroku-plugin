package com.felipefzdz.gradle.heroku.tasks;

import com.felipefzdz.gradle.heroku.heroku.api.OrganizationAppCreateRequest;
import com.heroku.api.Heroku;
import com.heroku.api.HerokuAPI;
import com.heroku.api.request.Request;
import org.gradle.api.DefaultTask;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

import java.time.Duration;

public class Deployer extends DefaultTask {
    private final Property<String> apiKey;
    private final Property<String> appName;
    private final Property<String> teamName;
    private final Property<Boolean> personalApp;
    private HerokuAPI herokuApi;

    public Deployer() {
        this.apiKey = getProject().getObjects().property(String.class);
        this.appName = getProject().getObjects().property(String.class);
        this.teamName = getProject().getObjects().property(String.class);
        this.personalApp = getProject().getObjects().property(Boolean.class);
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

    public void setTeamName(String teamName) {
        this.teamName.set(teamName);
    }

    public void setTeamName(Provider<String> teamName) {
        this.teamName.set(teamName);
    }

    @Input
    public String getTeamName() {
        return teamName.get();
    }

    public void setPersonalApp(Boolean personalApp) {
        this.personalApp.set(personalApp);
    }

    public void setPersonalApp(Provider<Boolean> personalApp) {
        this.personalApp.set(personalApp);
    }

    @Input
    public Boolean getPersonalApp() {
        return personalApp.get();
    }

    @TaskAction
    public void herokuDeploy() {
        herokuApi = new HerokuAPI(getApiKey());
        boolean recreate = false;
        maybeCreateApplication(getAppName(), getTeamName(), recreate);
        getLogger().quiet(String.format("Successfully deployed app %s", getAppName()));
    }

    private void maybeCreateApplication(String appName, String teamName, boolean recreate) {
        boolean exists = herokuApi.appExists(appName);
        if (exists && recreate) {
            getLogger().info(String.format("Destroying existing heroku app %s", appName));
            destroyApp(appName);
            exists = false;
        }
        if (!exists) {
            getLogger().info(String.format("Creating heroku app %s for team %s", appName, teamName));
            api3(new OrganizationAppCreateRequest(getAppName(), teamName, Heroku.Stack.Cedar14, getPersonalApp()));
        }
    }

    private void destroyApp(String appName) {
        getLogger().info(String.format("Destroying application %s", appName));
        herokuApi.destroyApp(appName);
        delay(Duration.ofSeconds(20));
    }

    private <T> T api3(Request<T> request) {
        return herokuApi.getConnection().execute(request, getApiKey());
    }

    private void delay(Duration duration) {
        getLogger().info(String.format("Delaying for %d milliseconds...", duration.toMillis()));
        sleep(duration.toMillis());
    }

    private void sleep(long duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            getLogger().error("Problem while sleeping", e);
        }
    }

}
