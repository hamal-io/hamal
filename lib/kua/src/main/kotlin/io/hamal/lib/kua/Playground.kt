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
            print(_G)
            for k,v in pairs(_G) do print(k,v) end
            
            local x = __decimal__.new(123)
            print(x)
            

        """.trimIndent()
        )
    }
}
