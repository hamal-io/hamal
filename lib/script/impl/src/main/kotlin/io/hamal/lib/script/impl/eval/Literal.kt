package io.hamal.lib.script.impl.eval

import io.hamal.lib.script.api.value.*
import io.hamal.lib.script.impl.ast.expr.*


internal class EvaluateNilLiteral<INVOKE_CTX : FuncInvocationContext> : Evaluate<NilLiteral, INVOKE_CTX> {
    override fun invoke(ctx: EvaluationContext<NilLiteral, INVOKE_CTX>) = NilValue
}

internal class EvaluateFalseLiteral<INVOKE_CTX : FuncInvocationContext> : Evaluate<FalseLiteral, INVOKE_CTX> {
    override fun invoke(ctx: EvaluationContext<FalseLiteral, INVOKE_CTX>) = FalseValue
}

internal class EvaluateTrueLiteral<INVOKE_CTX : FuncInvocationContext> : Evaluate<TrueLiteral, INVOKE_CTX> {
    override fun invoke(ctx: EvaluationContext<TrueLiteral, INVOKE_CTX>) = TrueValue
}

internal class EvaluateNumberLiteral<INVOKE_CTX : FuncInvocationContext> : Evaluate<NumberLiteral, INVOKE_CTX> {
    override fun invoke(ctx: EvaluationContext<NumberLiteral, INVOKE_CTX>) = ctx.toEvaluate.value
}

internal class EvaluateStringLiteral<INVOKE_CTX : FuncInvocationContext> : Evaluate<StringLiteral, INVOKE_CTX> {
    override fun invoke(ctx: EvaluationContext<StringLiteral, INVOKE_CTX>) = StringValue(ctx.toEvaluate.value)
}

internal class EvaluateCodeLiteral<INVOKE_CTX : FuncInvocationContext> : Evaluate<CodeLiteral, INVOKE_CTX> {
    override fun invoke(ctx: EvaluationContext<CodeLiteral, INVOKE_CTX>) = CodeValue(ctx.toEvaluate.value)
}

internal class EvaluatePrototypeLiteral<INVOKE_CTX : FuncInvocationContext> :
    Evaluate<PrototypeLiteral, INVOKE_CTX> {
    override fun invoke(ctx: EvaluationContext<PrototypeLiteral, INVOKE_CTX>): PrototypeValue {
        return PrototypeValue(
            ctx.evaluateAsIdentifier { ident },
            ctx.toEvaluate.parameters.map { ctx.evaluateAsString(it) },
            ctx.toEvaluate.block
        )
    }
}