package nz.org.geonet.gradle.build

import org.gradle.api.GradleScriptException

class BuildVersionExtension {

    String releaseTagPattern = "^release-(\\d+\\.\\d+\\.\\d+)"
    String matchGroup = "\$1"
    String versionSplitter = "."
    String snapShotQuantifier = "-SNAPSHOT"
    boolean isRelease = false
    boolean integrationVersion = false

    private GitVersion gitVersion

    public setGitVersion(GitVersion gitVersion) {
         this.gitVersion = gitVersion
    }

    String getVersion() {
        String version = null

        try {
            if (integrationVersion == false) {
                version = gitVersion.getBuildVersion(
                        releaseTagPattern,
                        matchGroup,
                        versionSplitter,
                        snapShotQuantifier,
                        isRelease
                )
            } else {
                version = gitVersion.integrationVersion()
            }
        } catch (Exception e) {
            throw new GradleScriptException("Cannot suss a build version for you.\n\n" +
                    "Using:\n\n" +
                    "releaseTagPattern: \"" + releaseTagPattern + "\"\n" +
                    "matchGroup: \"" + matchGroup + "\"\n" +
                    "versionSplitter: \"" + versionSplitter + "\"\n" +
                    "snapShotQuantifier: \"" + snapShotQuantifier + "\"\n" +
                    "isRelease: \"" + isRelease + "\"\n" +
                    "integrationVersion: \"" + integrationVersion + "\"\n" +
                    "git dir: \"" + gitVersion.gitDir + "\"\n" +
                    "\n" +
                    "Probable causes are:\n" +
                    "\n" +
                    "1. The directory is not a Git repo.\n" +
                    "2. There are no annotated tags matching the releaseTagPattern.\n" +
                    "3. The matchGroup and versionSplitter don't allow for the extraction of integers\n" +
                    "\n" +
                    "More info below\n\n"
                    , e)
        }

        return version
    }

}
