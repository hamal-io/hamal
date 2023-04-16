package io.hamal.script.interpreter

import io.hamal.script.ast.stmt.*
import io.hamal.script.value.*

internal object EvaluateStatement : Evaluate<Statement> {
    override fun invoke(toEvaluate: Statement, env: Environment) = when (toEvaluate) {
        is ExpressionStatement -> Evaluator.evaluate(toEvaluate, env)
        else -> TODO("Evaluation of $toEvaluate not supported yet")
    }
}

internal object EvaluateGlobalAssignment : Evaluate<Assignment.Global> {
    override fun invoke(toEvaluate: Assignment.Global, env: Environment): Value {
        val result = TableValue()
        //FIXME populate environment
        toEvaluate.identifiers.zip(toEvaluate.expressions)
            .forEach {
                result[StringValue(it.first)] = Evaluator.evaluate(it.second, env)
            }
        return result
    }
}

internal object EvaluateLocalAssignment : Evaluate<Assignment.Local> {
    override fun invoke(toEvaluate: Assignment.Local, env: Environment): Value {
        val result = TableValue()
        //FIXME populate environment
        toEvaluate.identifiers.zip(toEvaluate.expressions)
            .forEach {
                result[StringValue(it.first)] = Evaluator.evaluate(it.second, env)
            }
        return result
    }
}


internal object EvaluateBlock : Evaluate<Block> {
    override fun invoke(toEvaluate: Block, env: Environment): Value {
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
        env.assignLocal(value.identifier, value)
        return value
    }
}

internal object EvaluateReturn : Evaluate<Return> {
    override fun invoke(toEvaluate: Return, env: Environment) = Evaluator.evaluate(toEvaluate.expression, env)
}