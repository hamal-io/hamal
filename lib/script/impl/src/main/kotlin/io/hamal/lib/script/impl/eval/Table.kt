package io.hamal.lib.script.impl.eval

import io.hamal.lib.script.api.value.*
import io.hamal.lib.script.impl.ast.expr.*

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
        val tableIdentifier = ctx.toEvaluate.ident

        val target = ctx.env[tableIdentifier.value]
        if (target is EnvValue) {
            require(ctx.toEvaluate.parameter is TableKeyLiteral)
            return target[IdentValue(ctx.toEvaluate.parameter.value)]
        }

        val table = ctx.env[tableIdentifier.value] as TableValue
        //FIXME evaluate
        require(ctx.toEvaluate.parameter is TableKeyLiteral)
        return table.get(IdentValue(ctx.toEvaluate.parameter.value))
    }
}