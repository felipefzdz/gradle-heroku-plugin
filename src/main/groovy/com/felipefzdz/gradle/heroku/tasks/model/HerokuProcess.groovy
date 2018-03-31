package com.felipefzdz.gradle.heroku.tasks.model

import groovy.transform.ToString

@ToString
class HerokuProcess {
    String type
    int quantity
    String size
}
