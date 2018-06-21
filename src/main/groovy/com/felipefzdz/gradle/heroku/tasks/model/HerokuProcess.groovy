package com.felipefzdz.gradle.heroku.tasks.model

import groovy.transform.CompileStatic

@CompileStatic
class HerokuProcess {
    String type = ''
    int quantity = 0
    String size = 'standard-1X'

    @Override
    String toString() {
        return "HerokuProcess{" +
                "type='" + type + '\'' +
                ", quantity=" + quantity +
                ", size='" + size + '\'' +
                '}'
    }
}
