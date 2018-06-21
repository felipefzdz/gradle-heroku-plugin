package com.felipefzdz.gradle.heroku.tasks.model

import groovy.transform.CompileStatic

import java.util.function.Supplier

@CompileStatic
class BuildSource {
    String buildpackUrl = ''
    Supplier<String> buildUrl = {  -> '' } as Supplier<String>
    String buildVersion = ''

    @Override
    String toString() {
        return "BuildSource{" +
                "buildpackUrl='" + buildpackUrl + '\'' +
                ", buildUrl=" + buildUrl +
                ", buildVersion='" + buildVersion + '\'' +
                '}';
    }
}
