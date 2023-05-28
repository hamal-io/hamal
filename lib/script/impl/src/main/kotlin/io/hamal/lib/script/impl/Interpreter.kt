package io.hamal.lib.script.impl

import io.hamal.lib.script.api.ast.Statement
import io.hamal.lib.script.api.value.DepEnvironmentValue
import io.hamal.lib.script.api.value.DepValue
import io.hamal.lib.script.impl.eval.DefaultEvaluator
import io.hamal.lib.script.impl.eval.EvaluationContext

interface Interpreter {
    fun run(toEvaluate: Statement, env: DepEnvironmentValue): DepValue
}

object DefaultInterpreter : Interpreter {
    override fun run(toEvaluate: Statement, env: DepEnvironmentValue): DepValue {
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

