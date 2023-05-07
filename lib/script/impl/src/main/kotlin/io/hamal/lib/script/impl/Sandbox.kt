package io.hamal.lib.script.impl

import io.hamal.lib.script.api.Sandbox
import io.hamal.lib.script.api.value.Value
import io.hamal.lib.script.impl.ast.parse
import io.hamal.lib.script.impl.interpreter.Environment
import io.hamal.lib.script.impl.interpreter.Interpreter
import io.hamal.lib.script.impl.token.tokenize

class SandboxImpl(val env: Environment) : Sandbox {

    private val interpreter = Interpreter.DefaultImpl

    override fun eval(code: String): Value {
        val tokens = tokenize(code)
        val statements = io.hamal.lib.script.impl.ast.parse(tokens)
        try {
            return interpreter.run(statements, env)
        } catch (e: io.hamal.lib.script.impl.ScriptEvaluationException) {
            return e.error
        } catch (t: Throwable) {
            throw t
        }
    }

}
