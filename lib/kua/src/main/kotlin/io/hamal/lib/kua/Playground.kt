package io.hamal.lib.kua

import io.hamal.lib.kua.table.DefaultTableMap
import io.hamal.lib.kua.value.CodeValue
import io.hamal.lib.kua.value.StringValue


fun main() {
    FixedPathLoader.load()

    Sandbox().use { sb ->
        val bridge = sb.bridge
        sb.bridge.tableCreate(0, 0)
        sb.bridge.pushNil()
        sb.bridge.tableNext(-2)

        val t = DefaultTableMap(sb.bridge)
        println(t.set("a", StringValue("b")))
        println(t.set("b", StringValue("b")))
        println(t.set("c", StringValue("b")))
        sb.bridge.setGlobal("t")
        sb.bridge.getGlobal("t")
        println(t.set("d", StringValue("b")))

        bridge.pushNil()
        while (bridge.tableNext(-2)) {
            println(bridge.toString(-2))
            println(bridge.toString(-1))
            bridge.pop(1)
        }
        println(bridge.top())
        sb.runCode(
            CodeValue(
                """
                local x = {test = 23}
                for k, v in pairs(t) do
                  print(k..'='..v)
                end
                """.trimIndent()
            )
        )
    }
}