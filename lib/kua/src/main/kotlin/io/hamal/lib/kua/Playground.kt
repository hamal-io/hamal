package io.hamal.lib.kua

import io.hamal.lib.kua.NativeLoader.Preference.BuildDir
import io.hamal.lib.kua.builtin.Require
import io.hamal.lib.kua.function.*
import io.hamal.lib.kua.table.TableMapValue
import io.hamal.lib.kua.value.StringValue
import io.hamal.lib.kua.value.Value
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.Path
import kotlin.io.path.name

class Module(
    val name: String,
    val internals: List<Value>,
    val init: String
) : Value

fun Sandbox.registerModule(module: Module) {
    // register internal functions

    val funcs = module.internals
    val internalMap = tableCreateMap(1)
    funcs.filterIsInstance<NamedFunctionValue<*, *, *, *>>()
        .forEach { namedFunc ->
            bridge.pushFunctionValue(namedFunc.function)
            bridge.tabletSetField(internalMap.index, namedFunc.name)
        }

    // create a factory method
    this.load(module.init)

    // unload internals from _G
}

fun Sandbox.registerGlobalFunction(namedFn: NamedFunctionValue<*, *, *, *>) {
//    bridge.pushFunctionValue(namedFn.function)
    setGlobal(namedFn.name, namedFn.function)
}

fun main() {
    NativeLoader.load(BuildDir)
    Sandbox().also { sb ->

        sb.registerGlobalExtension(
            Extension(
                "extension", functions = listOf(
                    NamedFunctionValue("test",
                        object : Function0In0Out() {
                            override fun invoke(ctx: FunctionContext) {
                                println("Called - kotlin")
                            }
                        }),
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

        val registry = Registry()
        sb.registerGlobalFunction(NamedFunctionValue("require", Require(registry)))

//        sb.registerGlobalExtension(
//            Extension(
//                name = "test",
//                functions = listOf(
//                    NamedFunctionValue(
//                        "require", object : Function1In1Out<StringValue, TableMapValue>(
//                            FunctionInput1Schema(StringValue::class),
//                            FunctionOutput1Schema(TableMapValue::class)
//                        ) {
//                            override fun invoke(ctx: FunctionContext, arg1: StringValue): TableMapValue {
//                                println("importing module ${arg1}")
//
//                                sb.load(String(Files.readAllBytes(Path("/home/ddymke/Repo/hamal/lib/kua/src/main/resources/extension.lua"))))
//                                sb.load(
//                                    """
//                                    instance = ethFactory()
//                                """.trimIndent()
//                                )
//
//                                return ctx.getGlobalTableMap("instance")
//
////                                val result = ctx.tableCreateMap(1)
////
////                                return result
//                            }
//                        }
//                    )
//                )
//            )
//        )

    }.use { sb ->

        sb.load(
            String(Files.readAllBytes(Path("/home/ddymke/Repo/hamal/lib/kua/src/main/resources/out.lua")))
        )
    }


}

private val testPath = Paths.get("src", "main", "resources")
private fun collectFiles() = Files.walk(testPath).filter { f: Path -> f.name == "test.lua" }