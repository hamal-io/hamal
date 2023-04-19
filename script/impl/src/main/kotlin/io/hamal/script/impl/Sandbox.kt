package io.hamal.script.impl

import io.hamal.script.api.Sandbox
import io.hamal.script.api.value.Value
import io.hamal.script.impl.ast.parse
import io.hamal.script.impl.interpreter.Environment
import io.hamal.script.impl.interpreter.Interpreter
import io.hamal.script.impl.token.tokenize

class SandboxImpl(val env: Environment) : Sandbox {

    private val interpreter = Interpreter.DefaultImpl

    override fun eval(code: String): Value {
        val tokens = tokenize(code)
        val statements = parse(tokens)
        try {
            return interpreter.run(statements, env)
        } catch (e: io.hamal.script.impl.ScriptEvaluationException) {
            return e.error
        } catch (t: Throwable) {
            throw t
        }
    }

}
