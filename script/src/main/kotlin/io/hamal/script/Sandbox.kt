package io.hamal.script

import io.hamal.script.ast.parse
import io.hamal.script.interpreter.Environment
import io.hamal.script.interpreter.Interpreter
import io.hamal.script.token.tokenize
import io.hamal.script.value.Value

class Sandbox(val env: Environment) {

    private val interpreter = Interpreter.DefaultImpl


    fun eval(code: String): Value {
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
