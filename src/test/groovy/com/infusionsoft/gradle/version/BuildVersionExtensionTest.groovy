package com.infusionsoft.gradle.version

import static com.infusionsoft.gradle.version.TestData.getDefaultTagOptions
import static org.testng.Assert.assertEquals

import org.testng.annotations.Test

public class BuildVersionExtensionTest extends TestNGRepositoryTestCase {
  @Test
  void testGetVersionSucceedsIfGitAboveCurrentDirectory() {
    String expectedVersion = '0.0.1' + defaultTagOptions.releaseSuffix
    final unit = new BuildVersionExtension()
    unit.with {
      projectPath = db.directory
      isRelease = true
    }
    TestData.setupNewRepoWithSingleCommit(db)

    assertEquals(unit.version, expectedVersion)
  }
}
