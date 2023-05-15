package io.hamal.lib.script.impl.interpreter

import io.hamal.lib.script.api.Environment
import io.hamal.lib.script.api.native_.NativeFunction
import io.hamal.lib.script.api.value.FalseValue
import io.hamal.lib.script.api.value.NumberValue
import io.hamal.lib.script.api.value.TrueValue
import io.hamal.lib.script.api.value.Value
import io.hamal.lib.script.impl.ast.expr.*

internal object EvaluateCallExpression : Evaluate<CallExpression> {
    override fun invoke(toEvaluate: CallExpression, env: Environment): Value {
        val parameters = toEvaluate.parameters.map { Evaluator.evaluate(it, env) }

        require(env is RootEnvironment)

        env.findNativeFunction(toEvaluate.identifier)
            ?.let { fn ->
                return fn(
                    NativeFunction.Context(
                        parameters = parameters.zip(toEvaluate.parameters)
                            .map { NativeFunction.Parameter(it.first, it.second) },
                        env = env
                    )
                )
            }

        val prototype = env.findPrototype(toEvaluate.identifier)!!
        return Evaluator.evaluate(prototype.block, env)
    }
}

internal object EvaluateGroupedExpression : Evaluate<GroupedExpression> {
    override fun invoke(toEvaluate: GroupedExpression, env: Environment) =
        Evaluator.evaluate(toEvaluate.expression, env)

}

internal object EvaluateInfixExpression : Evaluate<InfixExpression> {
    override fun invoke(toEvaluate: InfixExpression, env: Environment): Value {
        val lhs = Evaluator.evaluate(toEvaluate.lhs, env)
        val rhs = Evaluator.evaluate(toEvaluate.rhs, env)
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

internal object EvaluateLiteralExpression : Evaluate<LiteralExpression> {
    override fun invoke(toEvaluate: LiteralExpression, env: Environment) = Evaluator.evaluate(toEvaluate, env)
}

internal object EvaluatePrefixExpression : Evaluate<PrefixExpression> {
    override fun invoke(toEvaluate: PrefixExpression, env: Environment): Value {
        val value = Evaluator.evaluate(toEvaluate.expression, env)
        return when (toEvaluate.operator) {
            // FIXME this must come from operator repository as well
            Operator.Minus -> NumberValue((value as NumberValue).value.negate())
            else -> TODO("Evaluation of operator ${toEvaluate.operator} not supported")
        }
    }

}