package io.hamal.testbed.api.workspace_admin

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


abstract class BaseApiWorkspaceAdminTest(apiUrl: String) : BaseTest(apiUrl) {

    @TestFactory
    fun run(): List<DynamicTest> {
        return testFiles()
            .sorted()
            .map { testPath ->
                val testName = generateTestName(testPath)
                dynamicTest(testName) {
                    val result = runTest(
                        testFile = testPath,
                        testEnv = HotObject.builder()
                            .set("token", "1-token")
                            .set("id", "1")
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
    Files.walk(Paths.get("src", "securityTest", "resources", "api", "workspace_admin"))
        .filter { f: Path -> f.name.endsWith(".lua") }
        .distinct()
        .sorted()
        .toList()
