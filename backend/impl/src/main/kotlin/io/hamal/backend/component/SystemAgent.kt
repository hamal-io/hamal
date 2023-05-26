package io.hamal.backend.component

import io.hamal.agent.extension.std.log.StdLogExtension
import io.hamal.backend.repository.api.domain.trigger.Trigger
import io.hamal.lib.script.api.value.EnvironmentValue
import io.hamal.lib.script.api.value.Identifier
import io.hamal.lib.script.impl.DefaultSandbox
import io.hamal.lib.script.impl.builtin.AssertFunction
import io.hamal.lib.script.impl.builtin.RequireFunction
import org.springframework.stereotype.Component

@Component
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

    fun run(trigger: Trigger) {
        try {
            val triggerEnv = EnvironmentValue(
                identifier = Identifier(trigger.name.value.value),
                global = globalEnv,
                parent = globalEnv
            )
            val sandbox = DefaultSandbox(triggerEnv)
            println(sandbox.eval(trigger.code.value))
        } catch (t: Throwable) {
            // FIXME log not throw
            t.printStackTrace()
        }
    }

}