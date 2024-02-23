package io.hamal.testbed.api.unauthenticated

import io.hamal.lib.common.hot.HotObject
import io.hamal.testbed.api.BaseTest
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.fail
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Stream
import kotlin.io.path.name

abstract class BaseApiUnauthenticatedTest(apiUrl: String) : BaseTest(apiUrl) {

    @TestFactory
    fun run(): List<DynamicTest> {
        return testFiles()
            .sorted()
            .map { testPath ->
                val testName = generateTestName(testPath)
                dynamicTest(testName) {
                    val result = runTest(testPath, HotObject.empty)
                    if (result is TestResult.Failure) {
                        fail { result.message }
                    }
                }
            }.toList()
    }

    private fun generateTestName(testPath: Path) = testPath.toAbsolutePath().toString().split("/")
        .dropWhile { it != "resources" }
        .drop(1)
        .joinToString("/")

    private fun testFiles(): Stream<Path> = Files.walk(testPath)
        .filter { f: Path -> f.name.endsWith(".lua") }
        .distinct()
        .sorted()

    private val testPath = Paths.get("src", "securityTest", "resources", "api", "unauthenticated")
}