package io.hamal.lib.script.impl.eval

import io.hamal.lib.script.api.value.*
import io.hamal.lib.script.impl.ast.expr.*

internal class EvaluateTableKeyLiteral<INVOKE_CTX : FuncInvocationContext> :
    Evaluate<TableKeyLiteral, INVOKE_CTX> {
    override fun invoke(ctx: EvaluationContext<TableKeyLiteral, INVOKE_CTX>): Value {
        return IdentValue(ctx.toEvaluate.value)
    }
}

internal class EvaluateTableIndexLiteral<INVOKE_CTX : FuncInvocationContext> :
    Evaluate<TableIndexLiteral, INVOKE_CTX> {
    override fun invoke(ctx: EvaluationContext<TableIndexLiteral, INVOKE_CTX>): Value {
        return NumberValue(ctx.toEvaluate.value)
    }
}


internal class EvaluateTableConstructor<INVOKE_CTX : FuncInvocationContext> :
    Evaluate<TableConstructorExpression, INVOKE_CTX> {
    override fun invoke(ctx: EvaluationContext<TableConstructorExpression, INVOKE_CTX>): Value {
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

internal class EvaluateTableAccess<INVOKE_CTX : FuncInvocationContext> : Evaluate<TableAccessExpression, INVOKE_CTX> {
    override fun invoke(ctx: EvaluationContext<TableAccessExpression, INVOKE_CTX>): Value {
        val key = ctx.evaluate(ctx.toEvaluate.key)

        return when (val target = ctx.evaluate(ctx.toEvaluate.target)) {
            is IdentValue -> {
                when (val table = ctx.env[target]) {
                    is EnvValue -> table[key as IdentValue]
                    is TableValue -> table[key]
                    else -> TODO()
                }
            }

            is TableValue -> target[key]
            else -> TODO()
        }
    }
}