package io.hamal.lib.script.impl.interpreter

import io.hamal.lib.script.api.value.NilValue
import io.hamal.lib.script.api.value.Value
import io.hamal.lib.script.impl.ast.stmt.*
import io.hamal.lib.script.impl.value.PrototypeValue

internal object EvaluateStatement : Evaluate<Statement> {
    override fun invoke(ctx: EvaluationContext<Statement>): Value {
        val (toEvaluate, env) = ctx
        return when (toEvaluate) {
            is ExpressionStatement -> Evaluator.evaluate(EvaluationContext(toEvaluate, env))
            else -> TODO("Evaluation of $toEvaluate not supported yet")
        }
    }
}

internal object EvaluateGlobalAssignment : Evaluate<Assignment.Global> {
    override fun invoke(ctx: EvaluationContext<Assignment.Global>): Value {
        val (toEvaluate, env) = ctx
        val identifiers = toEvaluate.identifiers.map { identifier ->
            Evaluator.evaluateAsIdentifier(
                EvaluationContext(
                    identifier,
                    env
                )
            )
        }
        val values = toEvaluate.expressions.map { expr -> Evaluator.evaluate(EvaluationContext(expr, env)) }
        require(identifiers.size == values.size)

        identifiers.zip(values).forEach { (identifier, value) -> env.addGlobal(identifier, value) }
        return NilValue
    }
}

internal object EvaluateLocalAssignment : Evaluate<Assignment.Local> {
    override fun invoke(ctx: EvaluationContext<Assignment.Local>): Value {
        val (toEvaluate, env) = ctx
        val identifiers = toEvaluate.identifiers.map { identifier ->
            Evaluator.evaluateAsIdentifier(
                EvaluationContext(
                    identifier,
                    env
                )
            )
        }
        val values = toEvaluate.expressions.map { expr -> Evaluator.evaluate(EvaluationContext(expr, env)) }
        require(identifiers.size == values.size)

        identifiers.zip(values).forEach { (identifier, value) -> env.addLocal(identifier, value) }
        return NilValue
    }
}


internal object EvaluateBlock : Evaluate<BlockStatement> {
    override fun invoke(ctx: EvaluationContext<BlockStatement>): Value {
        val (toEvaluate, env) = ctx
        var result: Value = NilValue
        for (statement in toEvaluate.statements) {
            result = Evaluator.evaluate(EvaluationContext(statement, env))
        }
        return result
    }
}

internal object EvaluateCall : Evaluate<Call> {
    override fun invoke(ctx: EvaluationContext<Call>): Value {
        val (toEvaluate, env) = ctx
        return Evaluator.evaluate(EvaluationContext(toEvaluate.expression, env))
    }
}

internal object EvaluateExpressionStatement : Evaluate<ExpressionStatement> {
    override fun invoke(ctx: EvaluationContext<ExpressionStatement>): Value {
        val (toEvaluate, env) = ctx
        return Evaluator.evaluate(EvaluationContext(toEvaluate.expression, env))
    }
}

internal object EvaluatePrototype : Evaluate<Prototype> {
    override fun invoke(ctx: EvaluationContext<Prototype>): Value {
        val (toEvaluate, env) = ctx

        val value = Evaluator.evaluate(EvaluationContext(toEvaluate.expression, env)) as PrototypeValue
        env.addLocal(value.identifier, value)
        return value
    }
}

internal object EvaluateReturn : Evaluate<Return> {
    override fun invoke(ctx: EvaluationContext<Return>): Value {
        val (toEvaluate, env) = ctx
        return Evaluator.evaluate(EvaluationContext(toEvaluate.expression, env))
    }
}