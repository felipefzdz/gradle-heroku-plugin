package com.felipefzdz.gradle.heroku

import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.AddAddonAttachmentsTask
import com.felipefzdz.gradle.heroku.tasks.model.HerokuAddonAttachment
import com.felipefzdz.gradle.heroku.tasks.model.HerokuWebApp
import com.felipefzdz.gradle.heroku.tasks.services.AddAddonAttachmentsService
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.internal.DefaultDomainObjectCollection
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.Subject

class AddAddonAttachmentsIntegTest extends Specification {

    @Rule
    TemporaryFolder temporaryFolder = new TemporaryFolder()

    @Subject
    AddAddonAttachmentsTask addAddonAttachments

    HerokuClient herokuClient = Mock(HerokuClient)

    String API_KEY = 'apiKey'
    String APP_NAME = 'appName'
    String PLAN = 'heroku-redis:hobby-dev'
    String OWNING_APP_NAME = 'owningAppName'

    def setup() {
        def project = ProjectBuilder.builder().withProjectDir(temporaryFolder.root).build()
        addAddonAttachments = project.tasks.create('addAddonAttachmentsTask', AddAddonAttachmentsTask)
        addAddonAttachments.addAddonAttachmentsService = new AddAddonAttachmentsService(herokuClient)
        def apiKeyProperty = project.objects.property(String)
        apiKeyProperty.set(API_KEY)
        addAddonAttachments.apiKey = apiKeyProperty

        def redisAddonAttachment = new HerokuAddonAttachment('redis')
        redisAddonAttachment.owningApp = OWNING_APP_NAME
        def addonAttachments = new DefaultDomainObjectCollection(HerokuAddonAttachment, [redisAddonAttachment]) as NamedDomainObjectContainer<HerokuAddonAttachment>

        def app = new HerokuWebApp(APP_NAME, null, addonAttachments)

        addAddonAttachments.app = app

    }

    def "add an addon attachment when missing"() {
        given:
        herokuClient.getAddonAttachments(APP_NAME) >> []
        herokuClient.getAddonAttachments(OWNING_APP_NAME) >> [['name': 'REDIS', 'addon': ['id': '1234']]]

        when:
        addAddonAttachments.addAddonAttachments()

        then:
        1 * herokuClient.createAddonAttachment(APP_NAME, _, 'REDIS')
    }

    def "skip adding an addon attachment when the addon doesn't exist on the owning app"() {
        given:
        herokuClient.getAddonAttachments(APP_NAME) >> []
        herokuClient.getAddonAttachments(OWNING_APP_NAME) >> []

        when:
        addAddonAttachments.addAddonAttachments()

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
        addAddonAttachments.addAddonAttachments()

        then:
        0 * herokuClient.installAddon(APP_NAME, PLAN)
    }
}
