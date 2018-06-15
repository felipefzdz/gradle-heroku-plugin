package com.felipefzdz.gradle.heroku

import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer

@CompileStatic
class HerokuExtension {

    NamedDomainObjectContainer<HerokuAppContainer> bundles

    HerokuExtension(NamedDomainObjectContainer<HerokuAppContainer> bundles) {
        this.bundles = bundles
    }

    void bundles(Action<? super NamedDomainObjectContainer<HerokuAppContainer>> action) {
        action.execute(bundles)
    }
}

