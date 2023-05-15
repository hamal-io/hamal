package io.hamal.lib.script.impl.interpreter

import io.hamal.lib.script.api.value.*
import io.hamal.lib.script.impl.ast.expr.*
import io.hamal.lib.script.impl.value.PrototypeValue

internal object EvaluateIdentifier : Evaluate<IdentifierLiteral> {
    override fun invoke(ctx: EvaluationContext<IdentifierLiteral>) = Identifier(ctx.toEvaluate.value)
}

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
    override fun invoke(ctx: EvaluationContext<NumberLiteral>) = NumberValue(ctx.toEvaluate.value)
}

internal object EvaluateStringLiteral : Evaluate<StringLiteral> {
    override fun invoke(ctx: EvaluationContext<StringLiteral>) = StringValue(ctx.toEvaluate.value)
}

internal object EvaluatePrototypeLiteral : Evaluate<PrototypeLiteral> {
    override fun invoke(ctx: EvaluationContext<PrototypeLiteral>): PrototypeValue {
        val (toEvaluate, env) = ctx
        return PrototypeValue(
            Evaluator.evaluateAsIdentifier(EvaluationContext(toEvaluate.identifier, env)),
            toEvaluate.parameters.map { Evaluator.evaluateAsString(EvaluationContext(it, env)) },
            toEvaluate.block

        )
    }
}