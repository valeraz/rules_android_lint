package com.example;

/** A test source with a DefaultLocale finding; only reported when every check runs on tests. */
public class GreeterTest {
  public String shout() {
    return new Greeter().greet("world").toUpperCase();
  }
}
