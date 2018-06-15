package com.felipefzdz.gradle.heroku.tasks.model

import groovy.transform.CompileStatic

import java.util.function.Supplier

@CompileStatic
class BuildSource {
    String buildpackUrl
    Supplier<String> buildUrl
    String buildVersion
}
