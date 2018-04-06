package com.felipefzdz.gradle.heroku

import com.felipefzdz.gradle.heroku.tasks.model.HerokuEnv
import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer

@CompileStatic
class HerokuExtension {

    NamedDomainObjectContainer<HerokuEnv> bundles
    HerokuAppContainer bundle

    HerokuExtension(NamedDomainObjectContainer<HerokuEnv> bundles, HerokuAppContainer bundle) {
        this.bundle = bundle
        this.bundles = bundles
    }

    void bundles(Action<? super NamedDomainObjectContainer<HerokuEnv>> action) {
        action.execute(bundles)
    }

    void bundle(Action<? super HerokuAppContainer> action) {
        action.execute(bundle)
    }
}

