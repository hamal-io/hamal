package io.hamal.module.worker.script.eval

import io.hamal.module.worker.script.ast.Expression
import io.hamal.module.worker.script.ast.LiteralExpression
import io.hamal.module.worker.script.ast.Statement
import io.hamal.module.worker.script.ast.StatementExpression
import io.hamal.module.worker.script.ast.expr.FalseLiteral
import io.hamal.module.worker.script.ast.expr.NumberLiteral
import io.hamal.module.worker.script.ast.expr.TrueLiteral
import io.hamal.module.worker.script.value.NilValue
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
                is StatementExpression -> evalExpression(statement.expression)
                else -> TODO()
            }
        }

    }
}

private fun evalExpression(expression: Expression): Value {
    return when (expression) {
        is LiteralExpression -> evalLiteral(expression)
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