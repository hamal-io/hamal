package io.hamal.lib.script.impl

import io.hamal.lib.script.api.value.DepEnvironmentValue
import io.hamal.lib.script.api.value.DepIdentifier


fun main() {
    val env = DepEnvironmentValue(DepIdentifier("_G"))
//    env.add()

    val box = DefaultSandbox(env)

    val result = box.eval(
        """
        local x = 10
    """.trimIndent()
    )

    println(result)
}