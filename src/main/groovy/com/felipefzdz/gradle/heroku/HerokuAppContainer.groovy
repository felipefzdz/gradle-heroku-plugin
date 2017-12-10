package com.felipefzdz.gradle.heroku

import com.felipefzdz.gradle.heroku.tasks.model.HerokuApp
import groovy.transform.CompileStatic
import org.gradle.api.internal.DefaultPolymorphicDomainObjectContainer
import org.gradle.internal.reflect.Instantiator

@CompileStatic
class HerokuAppContainer extends DefaultPolymorphicDomainObjectContainer<HerokuApp> {

    HerokuAppContainer(Instantiator instantiator) {
        super(HerokuApp, instantiator)
    }
}
