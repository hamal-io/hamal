package io.hamal.lib.kua

import io.hamal.lib.kua.value.CodeValue
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.name


class LuaTests {
    @TestFactory
    fun generateTestCases(): List<DynamicTest> {
        ResourceLoader.load()

        return collectFiles()
            .map { file ->
                dynamicTest("${file.parent.parent.name}/${file.parent.name}/${file.name}") {
                    val code = String(Files.readAllBytes(file))
                    val testInstance = LuaSandbox(LuaState())
                    testInstance.runCode(CodeValue(code))
                }
            }
            .toList()
    }

    private fun collectFiles() = Files.walk(testPath).filter { f: Path -> f.name.endsWith(".lua") }

    private val testPath = Paths.get("src", "integrationTest", "resources", "lua-5.4.6-tests")
}

