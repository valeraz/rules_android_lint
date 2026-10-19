package com.example;

/** A test source: calling a @RestrictTo(TESTS) API from here is allowed. */
public class TestOnlyApiTest {
  public int usesFixture() {
    return TestOnlyApi.fixture();
  }
}
