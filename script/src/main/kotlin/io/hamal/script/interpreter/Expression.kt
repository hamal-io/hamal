package io.hamal.script.interpreter

import io.hamal.script.ast.expr.*
import io.hamal.script.value.NumberValue
import io.hamal.script.value.Value

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

            else -> TODO()
        }
    }
}

internal object EvaluateLiteralExpression : Evaluate<LiteralExpression> {
    override fun invoke(toEvaluate: LiteralExpression, env: Environment) = Evaluator.evaluate(toEvaluate, env)
}

//
//        private fun evalPrefix(expression: PrefixExpression, env: Environment): Value {
//            val value = evalExpression(expression.value, env)
//            return when (expression.operator) {
//                // FIXME this must come from operator repository as well
//                Minus -> NumberValue((value as NumberValue).value.negate())
//                else -> TODO("${expression.operator} not supported")
//            }
//        }
//

internal object EvaluatePrefixExpression : Evaluate<PrefixExpression> {
    override fun invoke(toEvaluate: PrefixExpression, env: Environment): Value {
        val value = Evaluator.evaluate(toEvaluate.value, env)
        return when (toEvaluate.operator) {
            // FIXME this must come from operator repository as well
            Operator.Minus -> NumberValue((value as NumberValue).value.negate())
            else -> TODO("Evaluation of operator ${toEvaluate.operator} not supported")
        }
    }

}