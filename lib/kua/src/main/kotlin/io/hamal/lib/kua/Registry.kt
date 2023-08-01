package io.hamal.lib.kua

import io.hamal.lib.kua.function.FunctionValue
import io.hamal.lib.kua.table.TableMapValue
import io.hamal.lib.kua.value.Value

data class NewExt(
    val name: String, // FIXME VO
    val init: String,
    val internals: Map<String, Value>
)

class Registry(
    val sb: Sandbox
) {

    private val state = sb.state
    private val extensions = mutableMapOf<String, NewExt>()
    private val extensionFactories = mutableMapOf<String, TableMapValue>()

    fun register(extension: NewExt) {
        extensions[extension.name] = extension

        // FIXME load the factory
        loadFactory(extension.name)

    }

    fun loadFactory(name: String): TableMapValue {
        val extension = extensions[name]!!
        val internals = extension.internals

        val internalTable = state.tableCreateMap(internals.size)

        internals.forEach { entry ->
            val fn = entry.value
            require(fn is FunctionValue<*, *, *, *>)
            internalTable[entry.key] = fn
        }


//        internalTable["get_block_by_id"] = object : Function1In1Out<NumberValue, TableMapValue>(
//            FunctionInput1Schema(NumberValue::class),
//            FunctionOutput1Schema(TableMapValue::class)
//        ) {
//            override fun invoke(ctx: FunctionContext, arg1: NumberValue): TableMapValue {
//                print("getting the block")
//                return ctx.tableCreateMap(1).also { it["id"] = 42 }
//            }
//        }

        sb.setGlobal("_internal", internalTable)

        state.load(extensions[name]!!.init)
        state.load("_factory = create_extension_factory()")
        sb.unsetGlobal("_internal")

        // FIXME cache factory so that it does not have to be loaded over and over again
        val factory = state.getGlobalTableMap("_factory")
        extensionFactories[name] = factory

        state.unsetGlobal("extension")
        state.unsetGlobal("create_extension_factory")
        return factory
    }
}