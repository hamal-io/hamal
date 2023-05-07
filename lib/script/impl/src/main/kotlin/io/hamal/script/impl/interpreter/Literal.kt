package io.hamal.script.impl.interpreter

import io.hamal.script.api.value.*
import io.hamal.script.impl.ast.expr.*
import io.hamal.script.impl.value.*

internal object EvaluateIdentifier : Evaluate<Identifier> {
    override fun invoke(toEvaluate: Identifier, env: Environment) = StringValue(toEvaluate.value)
}

internal object EvaluateNilLiteral : Evaluate<NilLiteral> {
    override fun invoke(toEvaluate: NilLiteral, env: Environment) = NilValue
}

internal object EvaluateFalseLiteral : Evaluate<FalseLiteral> {
    override fun invoke(toEvaluate: FalseLiteral, env: Environment) = FalseValue
}

internal object EvaluateTrueLiteral : Evaluate<TrueLiteral> {
    override fun invoke(toEvaluate: TrueLiteral, env: Environment) = TrueValue
}

internal object EvaluateNumberLiteral : Evaluate<NumberLiteral> {
    override fun invoke(toEvaluate: NumberLiteral, env: Environment) = NumberValue(toEvaluate.value)
}

internal object EvaluateStringLiteral : Evaluate<StringLiteral> {
    override fun invoke(toEvaluate: StringLiteral, env: Environment) = StringValue(toEvaluate.value)
}

internal object EvaluatePrototypeLiteral : Evaluate<PrototypeLiteral> {
    override fun invoke(toEvaluate: PrototypeLiteral, env: Environment) = PrototypeValue(
        Evaluator.evaluateAsString(toEvaluate.identifier, env),
        toEvaluate.parameters.map { Evaluator.evaluateAsString(it, env) },
        toEvaluate.block
    )

}