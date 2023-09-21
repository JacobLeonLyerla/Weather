package com.example.jpmc.testUtil

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext

// Class for a JUnit extension which helps manage CoroutineDispatchers during testing.
class CoroutinesTestExtension @OptIn(ExperimentalCoroutinesApi::class) constructor(
    // This extension takes a TestDispatcher as a constructor parameter.
    // The dispatcher is defaulting to UnconfinedTestDispatcher which executes tasks immediately in the current thread
    // without any actual concurrency.
    val dispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : BeforeEachCallback, AfterEachCallback {

    // This function is called before each test execution.
    // It sets the provided dispatcher to be the main dispatcher (replaces Dispatchers.Main).
    // This is done because when running tests, you're not on the main Android thread,
    // and using Dispatchers.Main would cause an exception. By replacing the main dispatcher with our test dispatcher,
    // we can test the code that uses Dispatchers.Main.
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun beforeEach(context: ExtensionContext?) {
        Dispatchers.setMain(dispatcher)
    }

    // This function is called after each test execution.
    // It resets the main dispatcher to its original state,
    // to ensure that it doesn't continue to use our test dispatcher after the test is finished.
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun afterEach(context: ExtensionContext?) {
        Dispatchers.resetMain()
    }
}