package io.hamal.lib.script.impl.eval

import io.hamal.lib.script.api.value.*
import io.hamal.lib.script.impl.ast.expr.*

internal class EvaluateTableKeyLiteral<INVOKE_CTX : FuncInvocationContext> :
    Evaluate<TableKeyLiteral, INVOKE_CTX> {
    override fun invoke(ctx: EvaluationContext<TableKeyLiteral, INVOKE_CTX>): Value {
        return IdentValue(ctx.toEvaluate.value)
    }
}


internal class EvaluateTableConstructor<INVOKE_CTX : FuncInvocationContext> :
    Evaluate<TableConstructorExpression, INVOKE_CTX> {
    override fun invoke(ctx: EvaluationContext<TableConstructorExpression, INVOKE_CTX>): Value {
        val result: List<Pair<Value, Value>> = ctx.toEvaluate.fieldExpressions.map { fieldExpression ->
            if (fieldExpression is IndexFieldExpression) {
                NumberValue(fieldExpression.index.value) to ctx.evaluate(fieldExpression.value)
            } else {
                require(fieldExpression is KeyFieldExpression)
                IdentValue(fieldExpression.key) to ctx.evaluate(fieldExpression.value)
            }
        }
        return TableValue(result.toMap())
    }
}

internal class EvaluateTableAccess<INVOKE_CTX : FuncInvocationContext> : Evaluate<TableAccessExpression, INVOKE_CTX> {
    override fun invoke(ctx: EvaluationContext<TableAccessExpression, INVOKE_CTX>): Value {
        val key = ctx.evaluate(ctx.toEvaluate.key) as IdentValue

        return when (val target = ctx.evaluate(ctx.toEvaluate.target)) {
            is IdentValue -> {
                when (val table = ctx.env[target]) {
                    is EnvValue -> table[key]
                    is TableValue -> table[key]
                    else -> TODO()
                }
            }

            is TableValue -> target[key]
            else -> TODO()
        }
    }
}