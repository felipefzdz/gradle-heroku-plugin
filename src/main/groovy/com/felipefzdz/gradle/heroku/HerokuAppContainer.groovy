package com.felipefzdz.gradle.heroku

import com.felipefzdz.gradle.heroku.tasks.model.HerokuApp
import groovy.transform.CompileStatic
import org.gradle.api.Named
import org.gradle.api.internal.DefaultPolymorphicDomainObjectContainer
import org.gradle.internal.reflect.Instantiator
import org.gradle.api.internal.CollectionCallbackActionDecorator

@CompileStatic
class HerokuAppContainer extends DefaultPolymorphicDomainObjectContainer<HerokuApp> implements Named {

    private String name

    HerokuAppContainer(String name, Instantiator instantiator, CollectionCallbackActionDecorator callbackDecorator) {
        super(HerokuApp, instantiator, callbackDecorator)
        this.name = name
    }
    HerokuAppContainer(String name, Instantiator instantiator) {
        super(HerokuApp, instantiator)
        this.name = name
    }

    String getName() {
        return name
    }

    void setName(String name) {
        this.name = name
    }
}
