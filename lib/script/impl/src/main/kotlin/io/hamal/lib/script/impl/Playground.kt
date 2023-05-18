package io.hamal.lib.script.impl

import io.hamal.lib.script.api.value.EnvironmentValue
import io.hamal.lib.script.api.value.Identifier


fun main() {
    val env = EnvironmentValue(Identifier("_G"))
//    env.add()

    val box = DefaultSandbox(env)

    val result = box.eval(
        """
        local x = 10
    """.trimIndent()
    )

    println(result)
}