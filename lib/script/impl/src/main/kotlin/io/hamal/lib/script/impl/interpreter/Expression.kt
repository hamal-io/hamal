package io.hamal.lib.script.impl.interpreter

import io.hamal.lib.script.api.native_.NativeFunction
import io.hamal.lib.script.api.value.NumberValue
import io.hamal.lib.script.api.value.Value
import io.hamal.lib.script.impl.ast.expr.*

internal object EvaluateCallExpression : Evaluate<CallExpression> {
    override fun invoke(ctx: EvaluationContext<CallExpression>): Value {
        val toEvaluate = ctx.toEvaluate
        val env = ctx.env

        val parameters = toEvaluate.parameters.map { ctx.evaluate(it) }

        require(env is RootEnvironment) //FIXME REMOVE ME

        val identifier = ctx.evaluateAsIdentifier { identifier }
        env.findNativeFunction(identifier)
            ?.let { fn ->
                return fn(
                    NativeFunction.Context(
                        parameters = parameters.zip(toEvaluate.parameters)
                            .map { NativeFunction.Parameter(it.first, it.second) },
                        env = env
                    )
                )
            }

        val prototype = env.findPrototype(identifier)!!
        return ctx.evaluate(prototype.block)
    }
}

internal object EvaluateGroupedExpression : Evaluate<GroupedExpression> {
    override fun invoke(ctx: EvaluationContext<GroupedExpression>) = ctx.evaluate { expression }
}

internal object EvaluateInfixExpression : Evaluate<InfixExpression> {
    override fun invoke(ctx: EvaluationContext<InfixExpression>): Value {
        val lhs = ctx.evaluate { lhs }
        val rhs = ctx.evaluate { rhs }
        return ctx.evaluateInfix(ctx.toEvaluate.operator, lhs, rhs)
    }
}

internal object EvaluatePrefixExpression : Evaluate<PrefixExpression> {
    override fun invoke(ctx: EvaluationContext<PrefixExpression>): Value {
        val value = ctx.evaluate { expression }
        return when (val operator = ctx.toEvaluate.operator) {
            // FIXME this must come from operator repository as well
            Operator.Minus -> NumberValue((value as NumberValue).value.negate())
            else -> TODO("Evaluation of operator $operator not supported")
        }
    }

}