package com.felipefzdz.gradle.heroku

import com.felipefzdz.gradle.heroku.tasks.model.HerokuWebApp
import groovy.transform.CompileStatic
import org.gradle.api.internal.DefaultPolymorphicDomainObjectContainer
import org.gradle.internal.reflect.Instantiator

@CompileStatic
class HerokuAppContainer extends DefaultPolymorphicDomainObjectContainer<HerokuWebApp> {

    HerokuAppContainer(Instantiator instantiator) {
        super(HerokuWebApp, instantiator)
    }
}
