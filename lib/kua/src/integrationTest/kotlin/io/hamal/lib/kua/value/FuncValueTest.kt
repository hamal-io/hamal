package io.hamal.lib.kua.value

import io.hamal.lib.kua.FixedPathLoader
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.Bridge
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory

internal class FuncValueTest {
    //FIXME rewrite to use values
    @TestFactory
    fun `Returns single result`() = listOf(
        "Hamal Rocks"
    ).map {
        dynamicTest("Returns single value: $it") {

            val sandbox = Sandbox(FixedPathLoader)

            val returningFunc = object : FuncValue() {
                override fun invokedByLua(bridge: Bridge): Int {
                    bridge.pushString(it)
                    return 1
                }
            }

            val receiverFunc = object : FuncValue() {
                override fun invokedByLua(bridge: Bridge): Int {
                    result = bridge.toString(-1)
                    return 0
                }

                var result: String? = null
            }

            sandbox.register(
                ModuleValue(
                    name = "test",
                    namedFuncs = listOf(
                        NamedFuncValue("returns", returningFunc),
                        NamedFuncValue("receives", receiverFunc)
                    )
                )
            )

            sandbox.runCode(
                """
                local result = test.returns()
                test.receives(result)
            """.trimIndent()
            )

            assertThat(receiverFunc.result, equalTo("Hamal Rocks"))
        }
    }


    @TestFactory
    fun `Returns 2 results`() = listOf(
        Pair("ParamOne", "ParamTwo")
    ).map {
        dynamicTest("Returns single value: $it") {
            val sandbox = Sandbox(FixedPathLoader)

            val returningFunc = object : FuncValue() {
                override fun invokedByLua(bridge: Bridge): Int {
                    bridge.pushString(it.first)
                    bridge.pushString(it.second)
                    return 2
                }
            }

            val receiverFunc = object : FuncValue() {
                override fun invokedByLua(bridge: Bridge): Int {
                    resultOne = bridge.toString(-2)
                    resultTwo = bridge.toString(-1)
                    return 0
                }

                var resultOne: String? = null
                var resultTwo: String? = null
            }

            sandbox.register(
                ModuleValue(
                    name = "test",
                    namedFuncs = listOf(
                        NamedFuncValue("returns", returningFunc),
                        NamedFuncValue("receives", receiverFunc)
                    )
                )
            )

            sandbox.runCode(
                """
                local x,y = test.returns()
                test.receives(x,y)
            """.trimIndent()
            )

            assertThat(receiverFunc.resultOne, equalTo("ParamOne"))
            assertThat(receiverFunc.resultTwo, equalTo("ParamTwo"))
        }
    }

}
