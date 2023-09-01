package io.hamal.lib.kua

import io.hamal.lib.kua.NativeLoader.Preference.BuildDir
import io.hamal.lib.kua.function.FunctionType
import kotlin.reflect.KClass

fun Sandbox.registerGlobalFunction(name: String, function: FunctionType<*, *, *, *>) {
    setGlobal(name, function)
}


fun main() {
    NativeLoader.load(BuildDir)
    Sandbox(object : SandboxContext {
        override fun <OBJ : Any> get(clazz: KClass<OBJ>): OBJ {
            TODO("Not yet implemented")
        }
    }).use { sb ->
        sb.load(
            """
                local x = 512
                local y = __decimal__.new(1024)
                local z = __decimal__.new(512)
                
                -- assert(x ~= y)
                assert(y == x)
        """.trimIndent()
        )

        println(sb.top)
    }
}
