package io.hamal.testbed.api

import AbstractRunnerTest
import io.hamal.extension.net.http.ExtensionHttpFactory
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.domain.vo.RunnerEnv
import io.hamal.plugin.net.http.PluginHttpFactory
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
        return try {
            createTestRunner(
                pluginFactories = listOf(PluginHttpFactory()),
                extensionFactories = listOf(ExtensionHttpFactory),
                env = RunnerEnv(
                    HotObject.builder()
                        .set("test_api", apiUrl)
                        .build()
                )
            ).run(unitOfWork(String(Files.readAllBytes(testFile))))
            Success
        } catch (t: Throwable) {
            Failure(t.message ?: "Unknown error")
        }
    }
}