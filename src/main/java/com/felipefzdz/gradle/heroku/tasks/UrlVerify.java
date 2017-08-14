package com.felipefzdz.gradle.heroku.tasks;

import com.felipefzdz.gradle.heroku.http.DefaultHttpCaller;
import com.felipefzdz.gradle.heroku.http.HttpCallException;
import com.felipefzdz.gradle.heroku.http.HttpCaller;
import com.felipefzdz.gradle.heroku.http.HttpResponse;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.provider.PropertyState;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

public class UrlVerify extends DefaultTask {
    private HttpCaller httpCaller = new DefaultHttpCaller();
    private final PropertyState<String> url;

    public UrlVerify() {
        this.url = getProject().property(String.class);
    }

    public void setUrl(String url) {
        this.url.set(url);
    }

    public void setUrl(Provider<String> url) {
        this.url.set(url);
    }
    
    @Input
    public String getUrl() {
        return url.get();
    }

    @TaskAction
    public void verify() {
        try {
            HttpResponse httpResponse = httpCaller.get(getUrl());

            if (httpResponse.getCode() != 200) {
                throw new GradleException(String.format("Failed to resolve url '%s' (%s)", getUrl(), httpResponse.toString()));
            }
        } catch (HttpCallException e) {
            throw new GradleException(String.format("Failed to resolve url '%s'", getUrl(), e));
        }

        getLogger().quiet(String.format("Successfully resolved URL '%s'", getUrl()));
    }

    void setHttpCaller(HttpCaller httpCaller) {
        this.httpCaller = httpCaller;
    }
}
