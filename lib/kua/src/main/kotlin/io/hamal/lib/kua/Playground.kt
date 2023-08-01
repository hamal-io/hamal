package io.hamal.lib.kua

import io.hamal.lib.kua.NativeLoader.Preference.BuildDir
import io.hamal.lib.kua.builtin.Require
import io.hamal.lib.kua.function.*
import io.hamal.lib.kua.table.TableMapValue
import io.hamal.lib.kua.value.NumberValue
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.Path
import kotlin.io.path.name

fun Sandbox.registerGlobalFunction(namedFn: NamedFunctionValue<*, *, *, *>) {
    setGlobal(namedFn.name, namedFn.function)
}

fun main() {
    NativeLoader.load(BuildDir)
    Sandbox().also { sb ->

        val registry = Registry(sb)
        registry.register(
            NewExt(
                name = "web3/eth",
                init = String(Files.readAllBytes(Path("/home/ddymke/Repo/hamal/lib/kua/src/main/resources/extension.lua"))),
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

        sb.registerGlobalFunction(NamedFunctionValue("require", Require(registry)))

    }.use { sb ->
        sb.load(
            String(Files.readAllBytes(Path("/home/ddymke/Repo/hamal/lib/kua/src/main/resources/out.lua")))
        )
    }
}

private val testPath = Paths.get("src", "main", "resources")
private fun collectFiles() = Files.walk(testPath).filter { f: Path -> f.name == "test.lua" }