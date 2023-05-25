package io.hamal.backend.system_agent

import io.hamal.lib.domain.vo.Code
import io.hamal.lib.script.api.value.EnvironmentValue
import io.hamal.lib.script.api.value.Identifier
import io.hamal.lib.script.impl.DefaultSandbox

class SystemAgent {

    private val globalEnv = EnvironmentValue(
        identifier = Identifier("_G"),
        values = mapOf()
    )


    fun run(code: Code) {
        try {
            val env = EnvironmentValue(
                identifier = Identifier("TBD"), // FIXME
                global = globalEnv,
                parent = globalEnv
            )

            val sandbox = DefaultSandbox(globalEnv)
            println(sandbox.eval(code.value))
        } catch (t: Throwable) {
            // FIXME log not throw
            t.printStackTrace()
        }
    }


}