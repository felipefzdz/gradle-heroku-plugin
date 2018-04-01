package com.felipefzdz.gradle.heroku.tasks.model

import groovy.transform.CompileStatic
import org.gradle.internal.BiAction

@CompileStatic
class ReadinessProbe {
    String url
    BiAction<HerokuApp, Map<String, ?>> command
}
