package io.hamal.module.worker.script.eval

import io.hamal.module.worker.script.ast.*
import io.hamal.module.worker.script.ast.Operator.Plus
import io.hamal.module.worker.script.ast.expr.False
import io.hamal.module.worker.script.ast.expr.LiteralExpression
import io.hamal.module.worker.script.ast.expr.Number
import io.hamal.module.worker.script.ast.expr.True
import io.hamal.module.worker.script.ast.stmt.Block
import io.hamal.module.worker.script.value.*

interface Eval {

    operator fun invoke(statement: Statement, env: Environment): Value

    class DefaultImpl : Eval {
        override fun invoke(statement: Statement, env: Environment): Value {
            return when (statement) {
                is Block -> evalBlockStatement(statement, env)
                is ExpressionStatement -> evalExpression(statement.expression, env)
                else -> TODO()
            }
        }
    }
}

private fun Eval.DefaultImpl.evalBlockStatement(blockStatement: Block, env: Environment): Value {
    var result: Value = NilValue
    for (statement in blockStatement.statements) {
        result = invoke(statement, env)
    }
    return result
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
        is True -> TrueValue
        is False -> FalseValue
        is Number -> NumberValue(literal.value)
        else -> TODO()
    }
}