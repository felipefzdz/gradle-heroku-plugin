In order to functional test the plugin, and not get suspended by Heroku, we want to avoid hitting their API as much as possible.

The approach followed here is recording stateful scenarios with Wiremock, and serving those, instead of the real API.

There's plans to automate this process, but if you need to record new scenario you should:

1. Update the following on `BaseFuncTest`

```
WireMockRule wireMockRule = new WireMockRule()
def setupSpec() {
    wireMockRule.startRecording(WireMock.recordSpec().forTarget('https://api.heroku.com'))
}

def cleanupSpec() {
    wireMockRule.stopRecording()
}
```

2. Create an empty folder on `src/test/resources/mappings`

3. Run the test with:

```
HEROKU_HOST=http://localhost:8080
GRADLE_HEROKU_PLUGIN_API_KEY=xxx
```

4. Move the mappings folder into the appropriate one on `functTest` resources folder.

5. Restore `BaseFuncTest` original status.
