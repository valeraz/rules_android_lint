package com.rules.android.lint.examples

class LocaleTest {
    fun testStrings() {
        String.format("WRONG: %e", 1.0f)
    }
}