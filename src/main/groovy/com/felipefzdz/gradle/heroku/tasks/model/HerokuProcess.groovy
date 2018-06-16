package com.felipefzdz.gradle.heroku.tasks.model

import groovy.transform.CompileStatic

@CompileStatic
class HerokuProcess {
    String type
    int quantity
    String size

    @Override
    String toString() {
        return "HerokuProcess{" +
                "type='" + type + '\'' +
                ", quantity=" + quantity +
                ", size='" + size + '\'' +
                '}';
    }
}
