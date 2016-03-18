# Gradle Build Version Plugin

A plugin to find the build version from tags on a Git repo.

## Recommended Usage

The [pancakes-build-plugin](https://github.com/infusionsoft/pancakes-build-plugin) automatically
incorporates this version plugin, along with an *improved* default configuration for this plugin.
We recommend using the `pancakes-build-plugin` for any new pancakes-based project instead of
using this plugin directly.

## Quick Start

Tag releases in your Git repo with annotated tags like `release-1.0.0`. If you currently don't have a release tag the plugin will
 use `buildVersion.releaseTagIfNone` as the base, for example say `buildVersion.releaseTagIfNone = release-0.0.0` then the
 generated version will return `release-0.0.1`.

Add the build-version plugin to your build script and use the property `buildVersion.version` that it exposes to set the version
for your project:

```gradle
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
        classpath group: 'com.infusionsoft.gradle', name: 'build-version-plugin', version: '1.0.+'
    }
}

```

### SNAPSHOT Builds

The plugin makes assumptions based on the state of the git repositories regarding tags and commits. If as a developer I have
checked out a repository, made some changes, committed then built this would produce a SNAPSHOT. For example, the repo that
has been checked out has a HEAD with a tag `release-1.0.4` referencing it. Changes are committed and the project is built,
this will result in an artifact with the version `1.0.5<-SNAPSHOT>`, where `<-SNAPSHOT>` is the value of the
property `buildVersion.snapshotSuffix`.

### Release Builds.

Run the build with the system property `isRelease`. This will take the most recent tag, increment the least significant value,
append the `releaseSuffix` (if one has been configured) and tag the local working copy with a tag matching the `releaseTagPattern`.

```bash
./gradlew clean -DisRelease=true ...
```

For example, the repo gets checked out and is one or more commits ahead of the `release-1.0.5` tag. When the above gets called
the artifact will be built as version 1.0.6 and the local working copy HEAD will be tagged with `release-1.0.6`.

To make handling the tags a little easier we have supplied a `pushTags` task which will push all working copy tags up to origin,
completely optional although we recommend executing this task after the release artifact has been uploaded.

### Rebuild Previous Release Build.

Sometimes it is nice to check code out at a previous release to debug issues in production, in this case one would checkout
a release tag:

```bash
git checkout release-1.0.4
```

At this point the plugin will detect that HEAD is referenced by a release tag and will build the artifact as such. The resulting
artifact would have the version set as `1.0.4`.

NOTE: If one were to add some changes and leave them uncommitted for an experiment this would lead to the artifact being built with
the `snapshotSuffix` rather than the `releaseSuffix`.

## Configuration

The way that tags are matched can be configured.  This is very flexible so you can follow your own scheme, and if you need to,
change schemes on the same repo.

```gradle
apply plugin: 'build-version'

buildVersion {
    releaseTagPattern = "^release-(\\d+\\.\\d+\\.\\d)"
    releaseTagIfNone = "release-0.0.0"
    matchGroup = "\$1"
    versionSplitter = "."
    snapshotSuffix = "-SNAPSHOT"
    releaseSuffix = ""
}

version = buildVersion.version
```

* `releaseTagPattern` a regexp pattern to match the annotated release tags in your repo.  Make sure the version number part of tag
matches in a group ().
* `releaseTagIfNone` the plugin will use this as the previous release version when no release tag is found in the repo
* `matchGroup` the identifier of the group that matches the version number part of the tag.
* `versionSplitter` a string to use to split the version number part of the tag.  Must split it to return integers.
* `snapshotSuffix` a string to add to the end of the returned version number to identify a snapshot build.
* `releaseSuffix` a string to add to the end of the returned version number to identify a release build.

You can also set `isRelease` (boolean) in the buildVersion closure but it is most likely to be useful to set this by passing a System property
 on the command line:

```bash
./gradlew clean -DisRelease=true ...

```
A System property is used instead of a project property so that it is also available in init scripts when project may not
yet be available.  This is useful for changing resolve repos etc.

### Additional Tasks

In addition to the `pushTags` task mentioned above, the plugin adds a `currentVersion` task that performs
the same resolution strategy to determine what version would be produced by a `build` task and outputs
that version number to standard out.
