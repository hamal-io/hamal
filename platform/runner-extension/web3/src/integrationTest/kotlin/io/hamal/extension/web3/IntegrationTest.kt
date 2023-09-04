package io.hamal.extension.web3

import AbstractExtensionTest
import io.hamal.extension.web3.eth.EthExtensionFactory
import io.hamal.lib.kua.NativeLoader
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.name

object IntegrationTest : AbstractExtensionTest() {
    @TestFactory
    fun run(): List<DynamicTest> {
        NativeLoader.load(NativeLoader.Preference.Resources)
        return collectFiles().map { testFile ->
            DynamicTest.dynamicTest("${testFile.parent.name}/${testFile.name}") {
                val execute = createTestExecutor(EthExtensionFactory())
                execute.run(unitOfWork(String(Files.readAllBytes(testFile))))
            }
        }.toList()
    }
}

private val testPath = Paths.get("src", "integrationTest", "resources", "suite")
private fun collectFiles() = Files.walk(testPath).filter { f: Path -> f.name.endsWith(".lua") }