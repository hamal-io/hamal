package io.hamal.lib.script.impl

import io.hamal.lib.script.impl.eval.RootEnvironment


fun main() {
    val env = RootEnvironment()
//    env.add()

    val box = DefaultSandbox(env)

    val result = box.eval(
        """
        local x = 10
    """.trimIndent()
    )

    println(result)
}