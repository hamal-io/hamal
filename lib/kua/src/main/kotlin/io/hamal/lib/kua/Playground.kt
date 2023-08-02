package io.hamal.lib.kua

import io.hamal.lib.kua.NativeLoader.Preference.BuildDir
import io.hamal.lib.kua.builtin.Require
import io.hamal.lib.kua.extension.ExtensionRegistry
import io.hamal.lib.kua.extension.ScriptExtension
import io.hamal.lib.kua.function.*
import io.hamal.lib.kua.table.TableMapValue
import io.hamal.lib.kua.value.NumberValue

fun Sandbox.registerGlobalFunction(name: String, function: FunctionValue<*, *, *, *>) {
    setGlobal(name, function)
}

fun main() {
    NativeLoader.load(BuildDir)
    Sandbox().also { sb ->

        val registry = ExtensionRegistry(sb)
        registry.register(
            ScriptExtension(
                name = "web3/eth",
                init = """
                    function create_extension_factory()
                        local internal = _internal
                        return function()
                            return {
                                abi = {
                                    decode = function(type, value)
                                        print("decoding", type, value)
                                    end
                                },
                                call = function(arg1)
                                    return internal.get_block_by_id(arg1)
                                end
                            }
                        end
                    end
                """.trimIndent(),
                internals = mapOf(
                    "get_block_by_id" to object : Function1In1Out<NumberValue, TableMapValue>(
                        FunctionInput1Schema(NumberValue::class),
                        FunctionOutput1Schema(TableMapValue::class)
                    ) {
                        override fun invoke(ctx: FunctionContext, arg1: NumberValue): TableMapValue {
                            println("getting the block - ${arg1}")
                            return ctx.tableCreateMap(1).also { it["id"] = 42 }
                        }
                    }
                )
            )
        )

        sb.registerGlobalFunction("require", Require(registry))

    }.use { sb ->
        sb.load(
            """
                local eth = require('web3/eth')

                eth.config.update({host = "http://proxy"})
                print(eth.config.get())

                for k,v in pairs(eth.config.get()) do
                    print(k,v)
                end

                print(eth.call(23).id)
            """.trimIndent()
        )
    }
}
