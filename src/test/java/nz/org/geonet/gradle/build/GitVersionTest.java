package nz.org.geonet.gradle.build;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;

@RunWith(JUnit4.class)
public class GitVersionTest {

    static GitVersion buildVersion;

    @BeforeClass
    public static void setup() {
        buildVersion = new GitVersion("/home/geoffc/GeoNet/gradle-build-version-plugin");
    }

    @Test
    public void testGetLatestReleaseTag() throws Exception {
        Assert.assertTrue(buildVersion.getLatestReleaseTag("^release-\\d+\\.\\d+\\.\\d+$").matches("^release-\\d+\\.\\d+\\.\\d+$"));
        Assert.assertTrue(buildVersion.getLatestReleaseTag("^release-0.0.0$").matches("^release-0.0.0$"));
        Assert.assertTrue(buildVersion.getLatestReleaseTag("^(release-)(0.0.0)$").matches("^release-0.0.0$"));
    }

    @Test
    public void testGetNextSnapShotVersion() throws IOException, GitAPIException {
        Assert.assertEquals("0.0.1-SNAPSHOT", buildVersion.nextSnapShotVersion("release-0.0.0", "^(release-)(\\d+\\.\\d+\\.\\d+)$", "$2", ".", "-SNAPSHOT"));
        Assert.assertEquals("1.0.15-SNAPSHOT", buildVersion.nextSnapShotVersion("release-1.0.14", "^(release-)(\\d+\\.\\d+\\.\\d+)$", "$2", ".", "-SNAPSHOT"));
        Assert.assertEquals("0.0.1-SNAPSHOT", buildVersion.nextSnapShotVersion("release-0.0.0", "^release-(\\d+\\.\\d+\\.\\d+)$", "$1", ".", "-SNAPSHOT"));
        Assert.assertEquals("0.1-SNAPSHOT", buildVersion.nextSnapShotVersion("release-0.0", "^(release-)(\\d+\\.\\d+)$", "$2", ".", "-SNAPSHOT"));
        Assert.assertEquals("1-SNAPSHOT", buildVersion.nextSnapShotVersion("release-0", "^(release-)(\\d+)$", "$2", ".", "-SNAPSHOT"));
    }

    @Test(expected = NumberFormatException.class)
    public void testGetNextSnapShotVersionException(){
        Assert.assertEquals("0.0.1-SNAPSHOT", buildVersion.nextSnapShotVersion("release-0.0.0", "^(release-)(\\d+\\.\\d+\\.\\d)$", "$2", "fred", "-SNAPSHOT"));
    }

    @Test
    public void testReleaseVersion() throws Exception {
        Assert.assertEquals("0.0.0", buildVersion.releaseVersion("release-0.0.0", "^release-(\\d+\\.\\d+\\.\\d)$", "$1"));
    }

    @Test
    public void testGetBuildVersion() throws Exception {
        Assert.assertTrue(buildVersion.getBuildVersion("^(release-)(\\d+\\.\\d+\\.\\d)$", "$2", ".", "-SNAPSHOT", false).matches("\\d+\\.\\d+\\.\\d+-SNAPSHOT$"));
        Assert.assertTrue(buildVersion.getBuildVersion("^(release-)(\\d+\\.\\d+\\.\\d)$", "$2", ".", "-SNAPSHOT", true).matches("\\d+\\.\\d+\\.\\d+$"));
    }

    @Test
    public void testHeadTreeish() throws IOException, GitAPIException {
        // Not very easy to test in any meaningful way.
        Assert.assertTrue(buildVersion.headCommitTreeish().matches("\\w+"));
    }

    @Test
    public void testDateTimeUTC() {
        Assert.assertTrue(buildVersion.dateTimeUTC().matches("\\d{14}"));
    }

    @Test
    public void testIntegrationVersion() throws IOException, GitAPIException {
        if ("true".equals(System.getenv("SNAP_CI"))) {
            Assert.assertTrue(buildVersion.integrationVersion().matches("snap\\d+_git\\w{7}"));

        } else {
            Assert.assertTrue(buildVersion.integrationVersion().matches("\\d{14}_git\\w{7}"));
        }
    }
}
