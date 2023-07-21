package io.hamal.lib.kua

import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput2Schema
import io.hamal.lib.kua.table.TableProxy
import io.hamal.lib.kua.value.*


fun main() {
    FixedPathLoader.load()

    Sandbox().use { sb ->
        val bridge = sb.bridge
//        sb.bridge.tableCreate(0, 0)
//        sb.bridge.pushNil()
//        sb.bridge.tableNext(-2)


        sb.register(
            ExtensionValue(
                name = "test",
                functions = listOf(
                    NamedFunctionValue("call", TableTestFunc())
                )
            )
        )

//        val t = DefaultTableMap(sb.bridge)
//        println(t.set("a", StringValue("b")))
//        println(t.set("b", StringValue("b")))
//        println(t.set("c", StringValue("b")))
//        sb.bridge.setGlobal("t")
//        sb.bridge.getGlobal("t")
//        println(t.set("d", StringValue("b")))
//
//        bridge.pushNil()
//        while (bridge.tableNext(-2)) {
//            println(bridge.toString(-2))
//            println(bridge.toString(-1))
//            bridge.pop(1)
//        }


        sb.runCode(
            CodeValue(
                """
                local x = {abc = 'def'}
                local y = {}
                print(x)
                test.call(x, y)
                
                for k,v in pairs(x)
                do
                    print(k, v)
                end
               
               for k,v in pairs(y)
                do
                    print(k, v)
                end
                
                """.trimIndent()
            )
        )
    }
}


class TableTestFunc : Function2In0Out<TableProxy, TableProxy>(
    FunctionInput2Schema(TableProxy::class, TableProxy::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: TableProxy, arg2: TableProxy) {

        arg2["cool"] = StringValue("up")

        println(arg1.set("test", StringValue("works")))
        println(arg1.set("test1", StringValue("works")))
        println(arg1.set("test2", StringValue("works")))
        println(arg1.set("test3", StringValue("works")))


    }

}