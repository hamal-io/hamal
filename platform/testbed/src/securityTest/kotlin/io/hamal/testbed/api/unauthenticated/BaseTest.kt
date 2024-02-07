package io.hamal.testbed.api.unauthenticated

import io.hamal.lib.http.HttpTemplateImpl
import io.hamal.testbed.api.BaseTest
import io.hamal.testbed.api.BaseTest.TestResult.Success
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
        val apiHttpTemplate = HttpTemplateImpl(baseUrl = this.apiUrl)
        return testFiles()
            .sorted()
            .map { testPath ->
                val testName = generateTestName(testPath)
                dynamicTest(testName) {
//                    apiHttpTemplate.post("/v1/clear").execute()

                    var counter = 0
                    while (true) {
                        when (val result = runTest(testPath)) {
                            is Success -> break
                            is TestResult.Failure -> {
                                if (counter++ >= 3) {
                                    fail { result.message }
                                }
//                                apiHttpTemplate.post("/v1/clear").execute()
                            }
                        }
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