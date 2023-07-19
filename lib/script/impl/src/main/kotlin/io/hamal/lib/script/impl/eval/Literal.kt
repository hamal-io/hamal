package io.hamal.lib.script.impl.eval

import io.hamal.lib.kua.value.*
import io.hamal.lib.script.impl.ast.expr.*


internal class EvaluateNilLiteral : Evaluate<NilLiteral> {
    override fun invoke(ctx: EvaluationContext<NilLiteral>) = NilValue
}

internal class EvaluateFalseLiteral : Evaluate<FalseLiteral> {
    override fun invoke(ctx: EvaluationContext<FalseLiteral>) = FalseValue
}

internal class EvaluateTrueLiteral : Evaluate<TrueLiteral> {
    override fun invoke(ctx: EvaluationContext<TrueLiteral>) = TrueValue
}

internal class EvaluateNumberLiteral : Evaluate<NumberLiteral> {
    override fun invoke(ctx: EvaluationContext<NumberLiteral>) = ctx.toEvaluate.value
}

internal class EvaluateStringLiteral : Evaluate<StringLiteral> {
    override fun invoke(ctx: EvaluationContext<StringLiteral>) = StringValue(ctx.toEvaluate.value)
}

internal class EvaluateCodeLiteral : Evaluate<CodeLiteral> {
    override fun invoke(ctx: EvaluationContext<CodeLiteral>) = CodeValue(ctx.toEvaluate.value)
}

internal class EvaluatePrototypeLiteral :
    Evaluate<PrototypeLiteral> {
    override fun invoke(ctx: EvaluationContext<PrototypeLiteral>): PrototypeValue {
        return PrototypeValue(
            ctx.evaluateAsIdentifier { ident },
            ctx.toEvaluate.parameters.map { ctx.evaluateAsString(it) },
            ctx.toEvaluate.block
        )
    }
}