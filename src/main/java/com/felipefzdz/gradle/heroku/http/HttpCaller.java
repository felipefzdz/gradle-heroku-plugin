package com.felipefzdz.gradle.heroku.http;

public interface HttpCaller {
    HttpResponse get(String url);
}