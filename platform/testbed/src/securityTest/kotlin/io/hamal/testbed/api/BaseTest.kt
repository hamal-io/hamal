package io.hamal.testbed.api

import io.hamal.lib.common.value.ValueObject
import io.hamal.testbed.BaseTest
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.fail
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.name


abstract class ApiBaseSecurityTest(apiUrl: String) : BaseTest(apiUrl) {

    @TestFactory
    fun misc(): List<DynamicTest> {
        return miscFiles()
            .sorted()
            .map { testPath ->
                val testName = generateTestName(testPath)
                dynamicTest(testName) {
                    val result = runTest(testPath, ValueObject.empty)
                    if (result is TestResult.Failure) {
                        fail { result.message }
                    }
                }
            }.toList()
    }

    @TestFactory
    fun unauthenticated(): List<DynamicTest> {
        return unauthenticatedFiles()
            .sorted()
            .map { testPath ->
                val testName = generateTestName(testPath)
                dynamicTest(testName) {
                    val result = runTest(testPath, ValueObject.empty)
                    if (result is TestResult.Failure) {
                        fail { result.message }
                    }
                }
            }.toList()
    }

    @TestFactory
    fun unauthorized(): List<DynamicTest> {
        return unauthorizedFiles()
            .sorted()
            .flatMap { testPath ->
                val testName = generateTestName(testPath)
                listOf(
                    TestParameter("200", "Anonymous", "200-token"),
                    TestParameter("300", "User", "300-token")
                ).map { testParameter ->

                    dynamicTest("$testName - ${testParameter.name}") {
                        val result = runTest(
                            testFile = testPath,
                            testEnv = ValueObject.builder()
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

    @TestFactory
    fun workspace_admin(): List<DynamicTest> {
        return workspaceAdminFiles()
            .sorted()
            .map { testPath ->
                val testName = generateTestName(testPath)
                dynamicTest(testName) {
                    val result = runTest(
                        testFile = testPath,
                        testEnv = ValueObject.builder()
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

    private data class TestParameter(
        val id: String,
        val name: String,
        val token: String
    )
}

private fun generateTestName(testPath: Path) = testPath.toAbsolutePath().toString().split("/")
    .dropWhile { it != "resources" }
    .drop(1)
    .joinToString("/")


private fun miscFiles(): List<Path> =
    Files.walk(Paths.get("src", "securityTest", "resources", "api", "misc"))
        .filter { f: Path -> f.name.endsWith(".lua") }
        .distinct()
        .sorted()
        .toList()

private fun unauthorizedFiles(): List<Path> =
    Files.walk(Paths.get("src", "securityTest", "resources", "api", "unauthorized"))
        .filter { f: Path -> f.name.endsWith(".lua") }
        .distinct()
        .sorted()
        .toList()

private fun unauthenticatedFiles(): List<Path> =
    Files.walk(Paths.get("src", "securityTest", "resources", "api", "unauthenticated"))
        .filter { f: Path -> f.name.endsWith(".lua") }
        .distinct()
        .sorted()
        .toList()

private fun workspaceAdminFiles(): List<Path> =
    Files.walk(Paths.get("src", "securityTest", "resources", "api", "workspace_admin"))
        .filter { f: Path -> f.name.endsWith(".lua") }
        .distinct()
        .sorted()
        .toList()