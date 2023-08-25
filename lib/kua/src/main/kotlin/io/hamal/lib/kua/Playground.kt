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
            _D.digits(100)
            _D.pi=_D.new("3.1415926535897932384626433832795028841971693993751058209749445923078164062862090")
            print(_D.pi)

        """.trimIndent()
        )
    }
}
