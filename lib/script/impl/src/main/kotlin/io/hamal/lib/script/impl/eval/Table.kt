package io.hamal.lib.script.impl.eval

import io.hamal.lib.script.api.NativeEnvironment
import io.hamal.lib.script.api.value.Identifier
import io.hamal.lib.script.api.value.NumberValue
import io.hamal.lib.script.api.value.TableValue
import io.hamal.lib.script.api.value.Value
import io.hamal.lib.script.impl.ast.expr.*

internal object EvaluateTableConstructor : Evaluate<TableConstructorExpression> {
    override fun invoke(ctx: EvaluationContext<TableConstructorExpression>): Value {
        val result: List<Pair<Value, Value>> = ctx.toEvaluate.fieldExpressions.map { fieldExpression ->
            if (fieldExpression is IndexFieldExpression) {
                NumberValue(fieldExpression.index.value) to ctx.evaluate(fieldExpression.value)
            } else {
                require(fieldExpression is KeyFieldExpression)
                Identifier(fieldExpression.key) to ctx.evaluate(fieldExpression.value)
            }
        }
        return TableValue(result.toMap())
    }
}

internal object EvaluateTableAccess : Evaluate<TableAccessExpression> {
    override fun invoke(ctx: EvaluationContext<TableAccessExpression>): Value {
        val tableIdentifier = ctx.toEvaluate.identifier

        val target = ctx.env[tableIdentifier.value]
        if (target is NativeEnvironment) {
            require(ctx.toEvaluate.parameter is TableKeyLiteral)
            return target.get(Identifier(ctx.toEvaluate.parameter.value))
        }

        val table = ctx.env[tableIdentifier.value] as TableValue
        //FIXME evaluate
        require(ctx.toEvaluate.parameter is TableKeyLiteral)
        return table.get(Identifier(ctx.toEvaluate.parameter.value))
    }
}