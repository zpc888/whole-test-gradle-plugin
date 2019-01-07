# whole-test-gradle-plugin
Unit test helps only at component level, at high level, there are needs to do test multiple
components, or per functional set, or end to end by using on-demand test docker containers. 
To test them, we need create different types of tests, such as integrationTest, functionalTests,
performanceTest, dockerTest, etc.

These kinds of tests may fail often due to data out of date or function spec changing, their failure
should not block deployable artifact output, in some cases they may run after deploying build artifacts.

This plugin is to provide the ability to have integrationTest, functionalTest, performanceTest
under different folders, run after unit-test phase.

To use this plugin, please see: https://plugins.gradle.org/plugin/com.prot.wholetest

In short, for single project with gradle version greater than 2.1 and less than 4.0, using:
```gradle
plugins {
  id "com.prot.wholetest" version "0.2"         // for gradle 4.0 -
  // id "com.prot.wholetest" version "4.0"      // for gradle version 4.0 or 4.0+
}
```

For multi-project application, at root project, using:

```gradle
plugins {
  id "com.prot.wholetest" version "0.2" apply false         // for gradle 4.0 -
  // id "com.prot.wholetest" version "4.0" apply false      // for gradle version 4.0 or 4.0+
}
```

and then:
```gradle
allprojects {
   apply plugin 'com.prot.wholetest'
}
```

or (if only want to run for all sub-projects)
```gradle
subprojects {
   apply plugin 'com.prot.wholetest'
}
```

or (if only want to run for projects whose name starts with 'xyz')
```gradle
configure( allprojects.findAll { it.name.startsWith("xyz") } ) {
   apply plugin 'com.prot.wholetest'
}
```

or (another way)
```gradle
allprojects { prj ->
   if (prj.name.startsWith("xyz")) {
	   apply plugin 'com.prot.wholetest'
   }
}
```

Note: it only provides integrationTest with src/integTest/java and src/integTest/resources 
sourceSets for now. It can be easily expanded to different languages such as groovy, scala,
kotlin with different kinds of high level tests, such as performanceTest with 
src/perfTest/java and src/perfTest/resources.

