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
            -- print(_G)
            -- for k,v in pairs(_G) do print(k,v) end
            
            
            print(__decimal__)
            local x = __decimal__.new("3.1415926535897932384626433832795028841971693993751058209749445923078164062862090")
            print(x / 2)
            --for k,v in pairs(__decimal__) do print(k,v) end
            -- for k,v in pairs(_G) do print(k,v) end
            
            -- _decimal.digits(100)
            -- _decimal.pi=_decimal.new("3.1415926535897932384626433832795028841971693993751058209749445923078164062862090")
            -- print(_decimal.pi)
        """.trimIndent()
        )
    }
}
