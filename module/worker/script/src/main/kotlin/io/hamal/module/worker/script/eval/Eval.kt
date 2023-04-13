package io.hamal.module.worker.script.eval

import io.hamal.module.worker.script.ast.*
import io.hamal.module.worker.script.ast.Operator.Plus
import io.hamal.module.worker.script.ast.expr.FalseLiteral
import io.hamal.module.worker.script.ast.expr.NumberLiteral
import io.hamal.module.worker.script.ast.expr.TrueLiteral
import io.hamal.module.worker.script.value.NilValue
import io.hamal.module.worker.script.value.NumberValue
import io.hamal.module.worker.script.value.Value

interface Eval {

    operator fun invoke(statements: List<Statement>, env: Environment): Value

    operator fun invoke(statement: Statement, env: Environment): Value

    class DefaultImpl : Eval {
        override fun invoke(statements: List<Statement>, env: Environment): Value {
            var result: Value = NilValue
            for (statement in statements) {
                result = invoke(statement, env)
            }
            return result
        }

        override fun invoke(statement: Statement, env: Environment): Value {
            return when (statement) {
                is StatementExpression -> evalExpression(statement.expression, env)
                else -> TODO()
            }
        }

    }
}

private fun evalExpression(expression: Expression, env: Environment): Value {
    return when (expression) {
        is InfixExpression -> evalInfix(expression, env)
        is LiteralExpression -> evalLiteral(expression)
        else -> TODO()
    }
}

private fun evalInfix(expression: InfixExpression, env: Environment): Value {
    //FIXME error handling
    val lhs = evalExpression(expression.lhs, env)
    //FIXME error handling
    val rhs = evalExpression(expression.rhs, env)

    return eval(expression.operator, lhs, rhs, env)
}


private fun eval(operator: Operator, lhs: Value, rhs: Value, env: Environment): Value {
    //FIXME have an operator registry where operations of operators are specified based on the input --
    // should be a nice isolation for tests
    return when (operator) {
        Plus -> {
            NumberValue((lhs as NumberValue).value.plus((rhs as NumberValue).value))
        }

        else -> TODO()
    }
}

private fun evalLiteral(literal: LiteralExpression): Value {
    return when (literal) {
        is TrueLiteral -> literal.value
        is FalseLiteral -> literal.value
        is NumberLiteral -> literal.value
        else -> TODO()
    }
}