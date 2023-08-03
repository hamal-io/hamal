package io.hamal.extension.std.sys

import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.NativeLoader.Preference.Resources
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extension.ScriptExtension


fun main() {
    NativeLoader.load(Resources)

    val templateSupplier = { HttpTemplate("http://localhost:8008") }

    Sandbox().use {
        it.register(
            ScriptExtension(
                name = "sys",
                internals = mapOf(
                    "adhoc" to InvokeAdhocFunction(templateSupplier)
                )
            )
        )

        it.load(
            """
            local sys = require("sys")
            print(sys)
            local err, result = sys.adhoc({})
            print(err)
            print(result.id, result.status, result.exec_id)
            
        """.trimIndent()
        )
    }


}