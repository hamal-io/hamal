package io.hamal.lib.script.impl.eval

import io.hamal.lib.script.api.value.*
import io.hamal.lib.script.impl.ast.expr.*

internal object EvaluateTableConstructor : Evaluate<TableConstructorExpression> {
    override fun invoke(ctx: EvaluationContext<TableConstructorExpression>): DepValue {
        val result: List<Pair<DepValue, DepValue>> = ctx.toEvaluate.fieldExpressions.map { fieldExpression ->
            if (fieldExpression is IndexFieldExpression) {
                DepNumberValue(fieldExpression.index.value) to ctx.evaluate(fieldExpression.value)
            } else {
                require(fieldExpression is KeyFieldExpression)
                DepIdentifier(fieldExpression.key) to ctx.evaluate(fieldExpression.value)
            }
        }
        return DepTableValue(result.toMap())
    }
}

internal object EvaluateTableAccess : Evaluate<TableAccessExpression> {
    override fun invoke(ctx: EvaluationContext<TableAccessExpression>): DepValue {
        val tableIdentifier = ctx.toEvaluate.identifier

        val target = ctx.env[tableIdentifier.value]
        if (target is DepEnvironmentValue) {
            require(ctx.toEvaluate.parameter is TableKeyLiteral)
            return target[DepIdentifier(ctx.toEvaluate.parameter.value)]
        }

        val table = ctx.env[tableIdentifier.value] as DepTableValue
        //FIXME evaluate
        require(ctx.toEvaluate.parameter is TableKeyLiteral)
        return table.get(DepIdentifier(ctx.toEvaluate.parameter.value))
    }
}