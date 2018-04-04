package com.felipefzdz.gradle.heroku

import groovy.transform.CompileStatic
import org.gradle.api.Action

@CompileStatic
class HerokuExtension {

    HerokuAppContainer bundle

    HerokuExtension(HerokuAppContainer bundle) {
        this.bundle = bundle
    }

    void bundle(Action<? super HerokuAppContainer> action) {
        action.execute(bundle)
    }

}
