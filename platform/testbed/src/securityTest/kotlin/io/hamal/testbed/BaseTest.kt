package io.hamal.testbed

import io.hamal.extension.net.http.ExtensionHttpFactory
import io.hamal.lib.common.value.ValueObject
import io.hamal.lib.domain.vo.RunnerEnv
import io.hamal.plugin.net.http.PluginHttpFactory
import io.hamal.runner.test.AbstractRunnerTest
import io.hamal.testbed.BaseTest.TestResult.Failure
import io.hamal.testbed.BaseTest.TestResult.Success
import java.nio.file.Files
import java.nio.file.Path


abstract class BaseTest(private val apiUrl: String) : AbstractRunnerTest() {

    sealed interface TestResult {
        data object Success : TestResult
        data class Failure(val message: String) : TestResult
    }

    protected fun runTest(
        testFile: Path,
        testEnv: ValueObject
    ): TestResult {
        return try {
            createTestRunner(
                pluginFactories = listOf(PluginHttpFactory()),
                extensionFactories = listOf(ExtensionHttpFactory),
                env = RunnerEnv(
                    ValueObject.builder().also { builder ->
                        testEnv.properties.forEach { (key, value) ->
                            builder[key.stringValue] = value
                        }
                    }
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