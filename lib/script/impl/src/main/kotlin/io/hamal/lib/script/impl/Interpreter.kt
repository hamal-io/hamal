package io.hamal.lib.script.impl

import io.hamal.lib.script.api.ast.Statement
import io.hamal.lib.script.api.value.EnvValue
import io.hamal.lib.script.api.value.FuncInvocationContext
import io.hamal.lib.script.api.value.FuncInvocationContextFactory
import io.hamal.lib.script.api.value.Value
import io.hamal.lib.script.impl.eval.DefaultEvaluator
import io.hamal.lib.script.impl.eval.EvaluationContext

interface Interpreter<INVOKE_CTX : FuncInvocationContext> {
    fun run(toEvaluate: Statement, env: EnvValue): Value
}

class DefaultInterpreter<INVOKE_CTX : FuncInvocationContext>(
    private val funcInvocationContextFactory: FuncInvocationContextFactory<INVOKE_CTX>
) : Interpreter<INVOKE_CTX> {
    override fun run(toEvaluate: Statement, env: EnvValue): Value {
        val evaluator = DefaultEvaluator<INVOKE_CTX>()
        return evaluator.evaluate(
            EvaluationContext(
                toEvaluate = toEvaluate,
                env = env,
                evaluator = evaluator,
                funcInvocationContextFactory = funcInvocationContextFactory
            )
        )
    }
}

