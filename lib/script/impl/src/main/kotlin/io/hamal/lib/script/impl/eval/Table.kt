package io.hamal.lib.script.impl.eval

import io.hamal.lib.kua.value.*
import io.hamal.lib.script.impl.ast.expr.*

internal class EvaluateTableKeyLiteral :
    Evaluate<TableKeyLiteral> {
    override fun invoke(ctx: EvaluationContext<TableKeyLiteral>): Value {
        return IdentValue(ctx.toEvaluate.value)
    }
}

internal class EvaluateTableIndexLiteral :
    Evaluate<TableIndexLiteral> {
    override fun invoke(ctx: EvaluationContext<TableIndexLiteral>): Value {
        return NumberValue(ctx.toEvaluate.value)
    }
}


internal class EvaluateTableConstructor :
    Evaluate<TableConstructorExpression> {
    override fun invoke(ctx: EvaluationContext<TableConstructorExpression>): Value {
        val result: List<Pair<IdentValue, Value>> = ctx.toEvaluate.fieldExpressions.map { fieldExpression ->
            if (fieldExpression is IndexFieldExpression) {
                IdentValue((fieldExpression.index.value).toString()) to ctx.evaluate(fieldExpression.value)
            } else {
                require(fieldExpression is KeyFieldExpression)
                IdentValue(fieldExpression.key) to ctx.evaluate(fieldExpression.value)
            }
        }
        return TableValue(result.toMap())
    }
}

internal class EvaluateTableAccess : Evaluate<TableAccessExpression> {
    override fun invoke(ctx: EvaluationContext<TableAccessExpression>): Value {
        val key = ctx.evaluate(ctx.toEvaluate.key)

        return when (val target = ctx.evaluate(ctx.toEvaluate.target)) {
            is IdentValue -> {
                when (val table = ctx.env[target]) {
                    is EnvValue -> table[key as IdentValue]
                    is TableValue -> table[key]
                    is ErrorValue -> table[key as IdentValue]
                    else -> TODO()
                }
            }

            is TableValue -> target[key]
            else -> TODO()
        }
    }
}