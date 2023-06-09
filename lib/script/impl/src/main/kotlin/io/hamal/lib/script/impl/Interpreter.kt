package io.hamal.lib.script.impl

import io.hamal.lib.common.value.EnvValue
import io.hamal.lib.common.value.Value
import io.hamal.lib.script.api.ast.Statement
import io.hamal.lib.script.impl.eval.DefaultEvaluator
import io.hamal.lib.script.impl.eval.EvaluationContext

interface Interpreter {
    fun run(toEvaluate: Statement, env: EnvValue): Value
}

object DefaultInterpreter : Interpreter {
    override fun run(toEvaluate: Statement, env: EnvValue): Value {
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

