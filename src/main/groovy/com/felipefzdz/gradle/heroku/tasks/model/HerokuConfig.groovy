package com.felipefzdz.gradle.heroku.tasks.model

import groovy.transform.CompileStatic

@CompileStatic
class HerokuConfig {

    Map<String, String> configToBeExpected
    List<String> configToBeRemoved
    List<String> configToBeAdded
    List<String> configAddedByHeroku

    @Override
    String toString() {
        return "HerokuConfig{" +
                "configToBeExpected=" + configToBeExpected +
                ", configToBeRemoved=" + configToBeRemoved +
                ", configToBeAdded=" + configToBeAdded +
                ", configAddedByHeroku=" + configAddedByHeroku +
                '}';
    }
}
