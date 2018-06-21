package com.felipefzdz.gradle.heroku.tasks.model

import groovy.transform.CompileStatic
import org.gradle.internal.BiAction

@CompileStatic
class ReadinessProbe {
    String url = ''
    BiAction<HerokuApp, Map<String, ?>> command = ({ app, map -> } as BiAction<HerokuApp, Map<String, ?>>)

    @Override
    String toString() {
        return "ReadinessProbe{" +
                "url='" + url + '\'' +
                ", command=" + command +
                '}';
    }
}
