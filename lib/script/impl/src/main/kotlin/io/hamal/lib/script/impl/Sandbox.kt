package io.hamal.lib.script.impl

import io.hamal.lib.script.api.Sandbox
import io.hamal.lib.script.api.value.DepEnvironmentValue
import io.hamal.lib.script.api.value.DepValue
import io.hamal.lib.script.impl.ast.parse
import io.hamal.lib.script.impl.token.tokenize

class DefaultSandbox(
    private val env: DepEnvironmentValue
) : Sandbox {

    private val interpreter = DefaultInterpreter

    override fun eval(code: String): DepValue {
        val tokens = tokenize(code)
        val statements = parse(tokens)
        try {
            return interpreter.run(statements, env)
        } catch (e: ScriptEvaluationException) {
            return e.error
        } catch (t: Throwable) {
            throw t
        }
    }

}
