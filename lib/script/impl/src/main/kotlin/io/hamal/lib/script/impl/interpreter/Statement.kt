package io.hamal.lib.script.impl.interpreter

import io.hamal.lib.script.api.Environment
import io.hamal.lib.script.api.value.NilValue
import io.hamal.lib.script.api.value.Value
import io.hamal.lib.script.impl.ast.stmt.*
import io.hamal.lib.script.impl.value.PrototypeValue

internal object EvaluateStatement : Evaluate<Statement> {
    override fun invoke(toEvaluate: Statement, env: Environment) = when (toEvaluate) {
        is ExpressionStatement -> Evaluator.evaluate(toEvaluate, env)
        else -> TODO("Evaluation of $toEvaluate not supported yet")
    }
}

internal object EvaluateGlobalAssignment : Evaluate<Assignment.Global> {
    override fun invoke(toEvaluate: Assignment.Global, env: Environment): Value {
        val identifiers = toEvaluate.identifiers.map { identifier -> Evaluator.evaluateAsIdentifier(identifier, env) }
        val values = toEvaluate.expressions.map { expr -> Evaluator.evaluate(expr, env) }
        require(identifiers.size == values.size)

        identifiers.zip(values).forEach { (identifier, value) -> env.addGlobal(identifier, value) }
        return NilValue
    }
}

internal object EvaluateLocalAssignment : Evaluate<Assignment.Local> {
    override fun invoke(toEvaluate: Assignment.Local, env: Environment): Value {
        val identifiers = toEvaluate.identifiers.map { identifier -> Evaluator.evaluateAsIdentifier(identifier, env) }
        val values = toEvaluate.expressions.map { expr -> Evaluator.evaluate(expr, env) }
        require(identifiers.size == values.size)

        identifiers.zip(values).forEach { (identifier, value) -> env.addLocal(identifier, value) }
        return NilValue
    }
}


internal object EvaluateBlock : Evaluate<BlockStatement> {
    override fun invoke(toEvaluate: BlockStatement, env: Environment): Value {
        var result: Value = NilValue
        for (statement in toEvaluate.statements) {
            result = Evaluator.evaluate(statement, env)
        }
        return result
    }
}

internal object EvaluateCall : Evaluate<Call> {
    override fun invoke(toEvaluate: Call, env: Environment): Value {
        return Evaluator.evaluate(toEvaluate.expression, env)
    }
}

internal object EvaluateExpressionStatement : Evaluate<ExpressionStatement> {
    override fun invoke(toEvaluate: ExpressionStatement, env: Environment) =
        Evaluator.evaluate(toEvaluate.expression, env)
}

internal object EvaluatePrototype : Evaluate<Prototype> {
    override fun invoke(toEvaluate: Prototype, env: Environment): Value {
        val value = Evaluator.evaluate(toEvaluate.expression, env) as PrototypeValue
        env.addLocal(value.identifier, value)
        return value
    }
}

internal object EvaluateReturn : Evaluate<Return> {
    override fun invoke(toEvaluate: Return, env: Environment) = Evaluator.evaluate(toEvaluate.expression, env)
}