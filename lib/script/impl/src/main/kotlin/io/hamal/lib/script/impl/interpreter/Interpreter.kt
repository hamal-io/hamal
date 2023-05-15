package io.hamal.lib.script.impl.interpreter

import io.hamal.lib.script.api.Environment
import io.hamal.lib.script.api.value.Value
import io.hamal.lib.script.impl.ast.stmt.Statement

interface Interpreter {
    fun run(toEvaluate: Statement, env: Environment): Value
}

object DefaultInterpreter : Interpreter {
    override fun run(toEvaluate: Statement, env: Environment): Value {
        val evaluator = DefaultEvaluator()
        return evaluator.evaluate(
            EvaluationContext(
                toEvaluate = toEvaluate,
                env = env,
                evaluator = evaluator
            )
        )
    }
}

