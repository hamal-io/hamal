package io.hamal.testbed.api

import AbstractRunnerTest
import io.hamal.extension.net.http.HttpExtensionFactory
import io.hamal.plugin.net.http.HttpPluginFactory
import io.hamal.testbed.api.BaseTest.TestResult.Failure
import io.hamal.testbed.api.BaseTest.TestResult.Success
import java.nio.file.Files
import java.nio.file.Path

abstract class BaseTest(val apiUrl: String) : AbstractRunnerTest() {

    sealed interface TestResult {
        object Success : TestResult
        data class Failure(val message: String) : TestResult
    }

    protected fun runTest(testFile: Path): TestResult {
//        val config = ExtensionConfig(
//            mutableMapOf(
//                "base_url" to StringType(apiUrl)
//            )
//        )
        return try {
            createTestRunner(
                pluginFactories = listOf(HttpPluginFactory()),
                extensionFactories = listOf(HttpExtensionFactory)
            ).run(unitOfWork(String(Files.readAllBytes(testFile))))
            Success
        } catch (t: Throwable) {
            Failure(t.message ?: "Unknown error")
        }
    }
}