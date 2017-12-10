package com.felipefzdz.gradle.heroku.tasks.model

import groovy.transform.CompileStatic

@CompileStatic
class BuildSource {
    String buildpackUrl
    String buildUrl
    String buildVersion
}
