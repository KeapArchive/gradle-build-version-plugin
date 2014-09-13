# Gradle Build Version Plugin

A plugin to find the build version from tags on a Git repo.

## Quick Start

Tag releases in your Git repo with annotated tags like `release-1.0.0`.  If you are just starting development use a tag of
`release-0.0.0`.

Add the build-version plugin to your build script and use the property `buildVersion.version` that it exposes to set the version
for your project:

```
apply plugin: 'build-version'

version = buildVersion.version

buildscript {
    repositories {
        maven {
            url 'https://scm.infusiontest.com/nexus/content/groups/infusionsoft/'
        }
        mavenCentral()
    }
    dependencies {
        classpath group: 'com.infusionsoft.pancakes', name: 'gradle-build-version-plugin', version: '1.0.4'
    }
}

```

### SNAPSHOT Builds

Run with no other properties the plugin will search the Git repo for all tags that are like release-1.0.0, find the
latest one, strip out the version number, increment the minor version, append -SNAPSHOT to the revision and return it.  e.g.,
 if the latest matching tag on the repo is release-1.0.3 then the project version will be 1.0.4-SNAPSHOT.

### Release Builds.

Run the build with a system property e.g.,

```
./gradlew clean  -DisRelease=true ...
```

And for the above example (release-1.0.3 as the latest tag) the project version will be 1.0.3

## Configuration

The way that tags are matched can be configured.  This is very flexible so you can follow your own scheme, and if you need to,
change schemes on the same repo.

```
apply plugin: 'build-version'

buildVersion {
    releaseTagPattern = "^release-(\\d+\\.\\d+\\.\\d)"
    matchGroup = "\$1"
    versionSplitter = "."
    snapShotQuantifier = "-SNAPSHOT"
}

version = buildVersion.version

...
```

* `releaseTagPattern` a regexp pattern to match the annotated release tags in your repo.  Make sure the version number part of tag
matches in a group ().
* `matchGroup` the identifier of the group that matches the version number part of the tag.
* `versionSplitter` a string to use to split the version number part of the tag.  Must split it to return integers.
* `snapShotQuantifier` a string to add to the end of the returned version number to identify a snapshot build.

You can also set `isRelease` (boolean) in the buildVersion closure but it is most likely to be useful to set this by passing a System property
 on the command line:

```
./gradlew clean  -DisRelease=true ...

```
A System property is used instead of a project property so that it is also available in init scripts when project may not
yet be available.  This is useful for changing resolve repos etc.
