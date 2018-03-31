package com.felipefzdz.gradle.heroku

import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.model.HerokuAddonAttachment
import com.felipefzdz.gradle.heroku.tasks.services.AddAddonAttachmentsService
import spock.lang.Specification

class AddAddonAttachmentsTest extends Specification {

    AddAddonAttachmentsService addAddonAttachmentsService
    HerokuClient herokuClient = Mock(HerokuClient)

    String API_KEY = 'apiKey'
    String APP_NAME = 'appName'
    String PLAN = 'heroku-redis:hobby-dev'
    String OWNING_APP_NAME = 'owningAppName'
    List<HerokuAddonAttachment> addonAttachments

    def setup() {
        addAddonAttachmentsService = new AddAddonAttachmentsService(herokuClient)
        def redisAddonAttachment = new HerokuAddonAttachment('redis')
        redisAddonAttachment.owningApp = OWNING_APP_NAME
        addonAttachments = [redisAddonAttachment]
    }

    def "add an addon attachment when missing"() {
        given:
        herokuClient.getAddonAttachments(APP_NAME) >> []
        herokuClient.getAddonAttachments(OWNING_APP_NAME) >> [['name': 'REDIS', 'addon': ['id': '1234']]]

        when:
        addAddonAttachmentsService.addAddonAttachments(addonAttachments, API_KEY, APP_NAME)

        then:
        1 * herokuClient.createAddonAttachment(APP_NAME, _, 'REDIS')
    }

    def "skip adding an addon attachment when the addon doesn't exist on the owning app"() {
        given:
        herokuClient.getAddonAttachments(APP_NAME) >> []
        herokuClient.getAddonAttachments(OWNING_APP_NAME) >> []

        when:
        addAddonAttachmentsService.addAddonAttachments(addonAttachments, API_KEY, APP_NAME)

        then:
        def e = thrown(AssertionError)
        e.message == "Could not find an addon named REDIS belonging to the application owningAppName. Expression: addonResponse. Values: addonResponse = null"

        and:
        0 * herokuClient.installAddon(APP_NAME, PLAN)
    }

    def "skip adding an addon attachment when the attachment is already there"() {
        given:
        herokuClient.getAddonAttachments(APP_NAME) >> [['name': 'REDIS', 'addon': ['id': '1234']]]

        when:
        addAddonAttachmentsService.addAddonAttachments(addonAttachments, API_KEY, APP_NAME)

        then:
        0 * herokuClient.installAddon(APP_NAME, PLAN)
    }
}
