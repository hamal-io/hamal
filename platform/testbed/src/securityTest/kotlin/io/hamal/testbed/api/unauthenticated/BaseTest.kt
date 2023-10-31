package io.hamal.testbed.api.unauthenticated

import AbstractExtensionTest
import io.hamal.lib.kua.extension.ExtensionConfig
import io.hamal.lib.kua.type.StringType
import io.hamal.plugin.net.http.HttpPluginFactory
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.name

abstract class BaseApiUnauthenticatedTest(
    private val apiUrl: String
) : AbstractExtensionTest() {

    @TestFactory
    fun run(): List<DynamicTest> {
        return collectFiles().map { testFile ->
            dynamicTest("${testFile.parent.parent.name}/${testFile.parent.name}/${testFile.name}") {
                val config = ExtensionConfig(
                    mutableMapOf(
                        "base_url" to StringType(apiUrl)
                    )
                )
                createTestRunner(pluginFactories = listOf(HttpPluginFactory(config)))
                    .run(unitOfWork(String(Files.readAllBytes(testFile))))
            }
        }.toList()
    }


    private val testPath = Paths.get("src", "securityTest", "resources", "api", "unauthenticated")
    private fun collectFiles() = Files.walk(testPath).filter { f: Path -> f.name.endsWith(".lua") }
}