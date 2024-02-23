package io.hamal.testbed.api.unauthorized

import io.hamal.lib.common.hot.HotObject
import io.hamal.testbed.api.BaseTest
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.fail
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.name


abstract class BaseApiUnauthorizedTest(apiUrl: String) : BaseTest(apiUrl) {

    @TestFactory
    fun run(): List<DynamicTest> {
        return testFiles()
            .sorted()
            .flatMap { testPath ->
                val testName = generateTestName(testPath)
                listOf(
                    TestParameter("2", "Anonymous", "2-token"),
                    TestParameter("3", "User", "3-token")
                ).map { testParameter ->

                    dynamicTest("$testName - ${testParameter.name}") {
                        val result = runTest(
                            testFile = testPath,
                            testEnv = HotObject.builder()
                                .set("token", testParameter.token)
                                .set("id", testParameter.id)
                                .build()
                        )

                        if (result is TestResult.Failure) {
                            fail { result.message }
                        }
                    }
                }
            }
    }

    private data class TestParameter(
        val id: String,
        val name: String,
        val token: String
    )

    private fun generateTestName(testPath: Path) = testPath.toAbsolutePath().toString().split("/")
        .dropWhile { it != "resources" }
        .drop(1)
        .joinToString("/")

    private fun testFiles(): List<Path> =
        Files.walk(Paths.get("src", "securityTest", "resources", "api", "unauthorized"))
            .filter { f: Path -> f.name.endsWith(".lua") }
            .distinct()
            .sorted()
            .toList()
}