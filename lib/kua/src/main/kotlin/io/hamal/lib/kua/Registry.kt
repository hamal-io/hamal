package io.hamal.lib.kua

import io.hamal.lib.kua.function.*
import io.hamal.lib.kua.table.TableMapValue
import io.hamal.lib.kua.value.StringValue


data class NewExt(
    val name: String, // FIXME VO
    val init: String,
    val internals: List<NamedFunctionValue<*, *, *, *>>
)

// get loaded

class Registry(
    val state: State,
    val sb: Sandbox
) {

    private val extensions = mutableMapOf<String, NewExt>()
    private val extensionFactories = mutableMapOf<String, TableMapValue>()

    fun register(extension: NewExt) {
        extensions[extension.name] = extension

        // FIXME load the factory

    }

    fun loadFactory(name: String): TableMapValue {


        sb.registerGlobalExtension(
            Extension(
                "extension", functions = listOf(
                    NamedFunctionValue(
                        "import",
                        object : Function1In1Out<StringValue, TableMapValue>(
                            FunctionInput1Schema(StringValue::class),
                            FunctionOutput1Schema(TableMapValue::class)
                        ) {
                            override fun invoke(ctx: FunctionContext, arg1: StringValue): TableMapValue {
                                println("importing internals ${arg1.value}")
                                val result = ctx.tableCreateMap(1)
                                result["get_block"] = "placeholder"
                                return result
                            }
                        }
                    )
                )
            )
        )





        state.load(extensions[name]!!.init)
        state.load("_factory = create_extension_factory();")

        // FIXME cache factory so that it does not have to be loaded over and over again
        val factory = state.getGlobalTableMap("_factory")
//        state.setGlobal("_factory", factory)

//        state.unsetGlobal("_factory")
        state.unsetGlobal("extension")
        state.unsetGlobal("create_extension_factory")

        return factory
    }
}