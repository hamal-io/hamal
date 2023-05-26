package io.hamal.backend.system_agent

import io.hamal.agent.extension.std.log.StdLogExtension
import io.hamal.lib.domain.vo.Code
import io.hamal.lib.script.api.value.EnvironmentValue
import io.hamal.lib.script.api.value.Identifier
import io.hamal.lib.script.impl.DefaultSandbox
import io.hamal.lib.script.impl.builtin.AssertFunction
import io.hamal.lib.script.impl.builtin.RequireFunction

class SystemAgent {

    private val globalEnv = EnvironmentValue(
        identifier = Identifier("_G"),
        values = mapOf(
            AssertFunction.identifier to AssertFunction,
            RequireFunction.identifier to RequireFunction
        )
    )

    init {
        val logEnv = StdLogExtension().create()
        globalEnv.addGlobal(logEnv.identifier, logEnv)
    }

    fun run(code: Code) {
        try {
            val env = EnvironmentValue(
                identifier = Identifier("TBD_TRIGGER_NAME"), // FIXME
                global = globalEnv,
                parent = globalEnv
            )

            val sandbox = DefaultSandbox(env)
            println(sandbox.eval(code.value))
        } catch (t: Throwable) {
            // FIXME log not throw
            t.printStackTrace()
        }
    }


}