package io.hamal.lib.kua

import io.hamal.lib.kua.NativeLoader.Preference.BuildDir
import io.hamal.lib.kua.function.FunctionType
import io.hamal.lib.kua.type.DecimalType
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
        sb.native.pushDecimal(DecimalType(1))
        sb.native.pushDecimal(DecimalType(2))
//        sb.native.pushDecimal(DecimalType(3))
//
        println(sb.top)
        println(sb.native.toDecimalString(-2))

        sb.load(
            """
            local x = __decimal__.new(123)
            print(x / 2)
            local t = { value = 42 }
            local y = __decimal__.new(234)
            print(y / x)
        """.trimIndent()
        )

        println(sb.top)
    }
}
