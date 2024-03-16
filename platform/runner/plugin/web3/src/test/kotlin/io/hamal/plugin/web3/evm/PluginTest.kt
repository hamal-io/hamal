package io.hamal.plugin.web3.evm

import io.hamal.lib.kua.NativeLoader
import io.hamal.plugin.web3.evm.evm.PluginWeb3EvmFactory
import io.hamal.runner.test.AbstractRunnerTest
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.name

internal object PluginWeb3EvmTest : AbstractRunnerTest() {
    @TestFactory
    fun run(): List<DynamicTest> {
        NativeLoader.load(NativeLoader.Preference.Resources)
        return collectFiles().map { testFile ->
            dynamicTest("${testFile.parent.name}/${testFile.name}") {
                val runner = createTestRunner(pluginFactories = listOf(PluginWeb3EvmFactory()))
                runner.run(unitOfWork(String(Files.readAllBytes(testFile))))
            }
        }.toList()
    }
}

private val testPath = Paths.get("src", "test", "resources", "suite")
private fun collectFiles() = Files.walk(testPath).filter { f: Path -> f.name.endsWith(".lua") }