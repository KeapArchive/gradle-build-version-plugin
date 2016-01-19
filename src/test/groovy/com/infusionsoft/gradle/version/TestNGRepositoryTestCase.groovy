package com.infusionsoft.gradle.version

import org.eclipse.jgit.junit.RepositoryTestCase
import org.testng.annotations.BeforeMethod

abstract class TestNGRepositoryTestCase extends RepositoryTestCase {
  @BeforeMethod
  public void setUp() throws Exception {
    // superclass uses Junit @Before
    super.setUp();
  }
}
