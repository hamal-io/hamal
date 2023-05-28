package io.hamal.lib.script.impl.eval

import io.hamal.lib.script.api.value.DepIdentifier
import io.hamal.lib.script.api.value.DepValue
import io.hamal.lib.script.impl.ast.expr.IdentifierLiteral

internal object EvaluateIdentifier : Evaluate<IdentifierLiteral> {
    override fun invoke(ctx: EvaluationContext<IdentifierLiteral>): DepValue {
        return DepIdentifier(ctx.toEvaluate.value)
    }
}
