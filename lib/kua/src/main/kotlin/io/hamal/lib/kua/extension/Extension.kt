package io.hamal.lib.kua.extension

import io.hamal.lib.kua.function.NamedFunctionValue
import io.hamal.lib.kua.value.StringValue
import io.hamal.lib.kua.value.Value

interface ExtensionFactory {
    fun create(): Extension
}

// different kinds of extensions? like native extension(consisting of only native functions) and script extension(native + script)

class Extension(
    val name: String,
    val functions: List<NamedFunctionValue<*, *, *, *>>,
    val config: ExtensionConfig = ExtensionConfig(mutableMapOf()),
    val extensions: List<Extension> = listOf()
) : Value


data class NewExt(
    val name: String, // FIXME VO
    val init: String,
    val internals: Map<String, Value>,
    val config: ExtensionConfig = ExtensionConfig(
        mutableMapOf(
            "host" to StringValue("http://localhost:8008")
        )
    ),
) {

    fun getConfigFunction() = ExtensionGetConfigFunction(config)

    fun updateConfigFunction() = ExtensionUpdateConfigFunction(config)

}


