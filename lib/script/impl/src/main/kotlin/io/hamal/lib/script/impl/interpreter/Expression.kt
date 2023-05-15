package io.hamal.lib.script.impl.interpreter

import io.hamal.lib.script.api.Environment
import io.hamal.lib.script.api.native_.NativeFunction
import io.hamal.lib.script.api.value.FalseValue
import io.hamal.lib.script.api.value.NumberValue
import io.hamal.lib.script.api.value.TrueValue
import io.hamal.lib.script.api.value.Value
import io.hamal.lib.script.impl.ast.expr.*

internal object EvaluateCallExpression : Evaluate<CallExpression> {
    override fun invoke(ctx: EvaluationContext<CallExpression>): Value {
        val toEvaluate = ctx.toEvaluate
        val env = ctx.env

        val parameters = toEvaluate.parameters.map { Evaluator.evaluate(EvaluationContext(it, env)) }

        require(env is RootEnvironment)

        env.findNativeFunction(Evaluator.evaluateAsIdentifier(EvaluationContext(toEvaluate.identifier, env)))
            ?.let { fn ->
                return fn(
                    NativeFunction.Context(
                        parameters = parameters.zip(toEvaluate.parameters)
                            .map { NativeFunction.Parameter(it.first, it.second) },
                        env = env
                    )
                )
            }

        val identifier = Evaluator.evaluateAsIdentifier(EvaluationContext(toEvaluate.identifier, env))
        val prototype = env.findPrototype(identifier)!!
        return Evaluator.evaluate(EvaluationContext(prototype.block, env))
    }
}

internal object EvaluateGroupedExpression : Evaluate<GroupedExpression> {
    override fun invoke(ctx: EvaluationContext<GroupedExpression>) =
        Evaluator.evaluate(EvaluationContext(ctx.toEvaluate.expression, ctx.env))

}

internal object EvaluateInfixExpression : Evaluate<InfixExpression> {
    override fun invoke(ctx: EvaluationContext<InfixExpression>): Value {
        val (toEvaluate, env) = ctx
        val lhs = Evaluator.evaluate(EvaluationContext(toEvaluate.lhs, env))
        val rhs = Evaluator.evaluate(EvaluationContext(toEvaluate.rhs, env))
        return eval(toEvaluate.operator, lhs, rhs, env)
    }

    private fun eval(operator: Operator, lhs: Value, rhs: Value, env: Environment): Value {
        //FIXME have an operator registry where operations of operators are specified based on the input --
        // should be a nice isolation for tests
        return when (operator) {
            Operator.Plus -> {
                NumberValue((lhs as NumberValue).value.plus((rhs as NumberValue).value))
            }

            Operator.Minus -> {
                NumberValue((lhs as NumberValue).value.minus((rhs as NumberValue).value))
            }

            Operator.LessThan -> {
                println(lhs)
                if ((lhs as NumberValue).value.isLessThan((rhs as NumberValue).value)) {
                    return TrueValue
                } else {
                    return FalseValue
                }
            }


            else -> TODO()
        }
    }
}

internal object EvaluatePrefixExpression : Evaluate<PrefixExpression> {
    override fun invoke(ctx: EvaluationContext<PrefixExpression>): Value {
        val (toEvaluate, env) = ctx
        val value = Evaluator.evaluate(EvaluationContext(toEvaluate.expression, env))
        return when (toEvaluate.operator) {
            // FIXME this must come from operator repository as well
            Operator.Minus -> NumberValue((value as NumberValue).value.negate())
            else -> TODO("Evaluation of operator ${toEvaluate.operator} not supported")
        }
    }

}