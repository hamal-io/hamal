package io.hamal.lib.nodes

import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.SandboxContextNop
import io.hamal.lib.kua.extend.extension.RunnerExtension
import io.hamal.lib.kua.type.KuaCode
import io.hamal.lib.kua.type.KuaString
import io.hamal.lib.nodes.compiler.Compiler
import org.junit.jupiter.api.Test

internal class IntOutTest : BaseSandboxTest() {

    @Test
    fun test() {
        testInstance.register(
            RunnerExtension(
                name = KuaString("some_plugin"),
                factoryCode = KuaCode(
                    """
                     function extension_create()
                        local export = { 
                            magic = function() end
                        }
                        return export
                    end
                """.trimIndent()
                ),
            )
        )

        val code = testCompiler.compile(Nodes())
        testInstance.codeLoad(KuaCode(code))
    }

}

internal sealed class BaseSandboxTest {
    val testInstance = run {
        NativeLoader.load(NativeLoader.Preference.Resources)
        Sandbox(SandboxContextNop)
    }

    val testCompiler = Compiler
}
