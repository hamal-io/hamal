package io.hamal.extension.std.algo

import io.hamal.runner.test.RunnerFixture.createTestRunner
import io.hamal.runner.test.RunnerFixture.unitOfWork
import io.hamal.runner.test.TestConnector
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Stream
import kotlin.io.path.name

class UnitTest {

    @TestFactory
    fun run(): List<DynamicTest> = testPaths()
        .sorted()
        .map { testPath -> dynamicTest(generateTestName(testPath)) { runTest(testPath) } }
        .toList()

    private fun runTest(testPath: Path) {
        val files = Files.walk(testPath)
            .filter { f: Path -> f.name.endsWith(".lua") }
            .sorted()

        for (file in files) {
            println(">>>>>>>>>>>>>> ${file.fileName}")

            createTestRunner(
                connector = TestConnector(),
                extensionFactories = listOf(ExtensionStdAlgoFactory)
            ).run(unitOfWork(Files.readString(file)))

        }
    }

    private fun generateTestName(testPath: Path) = testPath.toAbsolutePath().toString().split("/")
        .dropWhile { it != "resources" }
        .drop(1)
        .joinToString("/")

    private fun testPaths(): Stream<Path> = Files.walk(testPath)
        .filter { f: Path -> f.name.endsWith(".lua") }
        .distinct()
        .sorted()

    private val testPath: Path = Paths.get("src", "test", "resources")
}