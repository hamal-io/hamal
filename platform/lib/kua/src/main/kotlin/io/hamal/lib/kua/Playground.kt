package io.hamal.lib.kua

import io.hamal.lib.kua.NativeLoader.Preference.BuildDir
import io.hamal.lib.kua.type.KuaCode
import io.hamal.lib.kua.type.KuaFunction
import io.hamal.lib.value.ValueString

fun Sandbox.registerGlobalFunction(name: String, function: KuaFunction<*, *, *, *>) {
    globalSet(ValueString(name), function)
}


fun main() {
    NativeLoader.load(BuildDir)
    Sandbox(SandboxContextNop).use { sb ->
        sb.codeLoad(
            KuaCode(
                """
                local x = 512
                local y = __decimal__.new(1024)
                local z = __decimal__.new(512)
                
                -- assert(x ~= y)
                assert(y == x)
        """.trimIndent()
            )
        )
    }
}
