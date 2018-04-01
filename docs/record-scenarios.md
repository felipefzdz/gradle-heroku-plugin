In order to functional test the plugin, and not get suspended by Heroku, we want to avoid hitting their API as much as possible.

The approach followed here is recording stateful scenarios with Wiremock, and serving those, instead of the real API.

There's plans to increase the automation of this process, but if you need to record new scenario you should:

1. Run a particular test with the following property enabled:

```
GRADLE_HEROKU_PLUGIN_API_KEY=xxx && ./gradlew functionalTest --tests '*DeployBundleFuncTest*' -PrecordScenarios
``` 

2. Move the mappings folder on `src/test/resources/mappings` into the appropriate one on `functTest` resources folder.

3. Now, you should be able to see the test running against that recorded scenario:

```
./gradlew functionalTest --tests '*DeployBundleFuncTest*' 
```
