package com.felipefzdz.gradle.heroku.tasks.model

import com.felipefzdz.gradle.heroku.HerokuAppContainer
import groovy.transform.CompileStatic
import org.gradle.api.Action

@CompileStatic
class HerokuEnv {
    final String name
    HerokuAppContainer bundle

    HerokuEnv(String name, HerokuAppContainer bundle) {
        this.name = name
        this.bundle = bundle
    }

    void bundle(Action<? super HerokuAppContainer> action) {
        action.execute(bundle)
    }

}
