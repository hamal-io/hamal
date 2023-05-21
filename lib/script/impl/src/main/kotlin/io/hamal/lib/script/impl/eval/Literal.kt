package io.hamal.lib.script.impl.eval

import io.hamal.lib.script.api.value.*
import io.hamal.lib.script.impl.ast.expr.*


internal object EvaluateNilLiteral : Evaluate<NilLiteral> {
    override fun invoke(ctx: EvaluationContext<NilLiteral>) = NilValue
}

internal object EvaluateFalseLiteral : Evaluate<FalseLiteral> {
    override fun invoke(ctx: EvaluationContext<FalseLiteral>) = FalseValue
}

internal object EvaluateTrueLiteral : Evaluate<TrueLiteral> {
    override fun invoke(ctx: EvaluationContext<TrueLiteral>) = TrueValue
}

internal object EvaluateNumberLiteral : Evaluate<NumberLiteral> {
    override fun invoke(ctx: EvaluationContext<NumberLiteral>) = ctx.toEvaluate.value
}

internal object EvaluateStringLiteral : Evaluate<StringLiteral> {
    override fun invoke(ctx: EvaluationContext<StringLiteral>) = StringValue(ctx.toEvaluate.value)
}

internal object EvaluatePrototypeLiteral : Evaluate<PrototypeLiteral> {
    override fun invoke(ctx: EvaluationContext<PrototypeLiteral>): PrototypeValue {
        return PrototypeValue(
            ctx.evaluateAsIdentifier { identifier },
            ctx.toEvaluate.parameters.map { ctx.evaluateAsString(it) },
            ctx.toEvaluate.block
        )
    }
}