package io.hamal.lib.script.impl.eval

import io.hamal.lib.common.value.IdentValue
import io.hamal.lib.common.value.NilValue
import io.hamal.lib.common.value.Value
import io.hamal.lib.script.api.ast.Statement
import io.hamal.lib.script.impl.ast.stmt.*

internal object EvaluateStatement : Evaluate<Statement> {
    override fun invoke(ctx: EvaluationContext<Statement>): Value {
        return when (val toEvaluate = ctx.toEvaluate) {
            is ExpressionStatement -> ctx.evaluate(toEvaluate)
            else -> TODO("Evaluation of $toEvaluate not supported yet")
        }
    }
}

internal object EvaluateGlobalAssignment : Evaluate<Assignment.Global> {
    override fun invoke(ctx: EvaluationContext<Assignment.Global>): Value {
        val identifiers = ctx.toEvaluate.identifiers.map(ctx::evaluateAsIdentifier)
        val values = ctx.toEvaluate.expressions.map(ctx::evaluate)
        identifiers.zip(values).forEach { (identifier, value) ->
            if (value is IdentValue) {
                ctx.env.addGlobal(identifier, ctx.env[value]) //FIXME create a copy if needed - like value is a table
            } else {
                ctx.env.addGlobal(identifier, value)
            }
        }
        return NilValue
    }
}

internal object EvaluateLocalAssignment : Evaluate<Assignment.Local> {
    override fun invoke(ctx: EvaluationContext<Assignment.Local>): Value {
        val identifiers = ctx.toEvaluate.identifiers.map(ctx::evaluateAsIdentifier)
        val values = ctx.toEvaluate.expressions.map(ctx::evaluate)
        identifiers.zip(values).forEach { (identifier, value) ->
            if (value is IdentValue) {
                ctx.env.addLocal(identifier, ctx.env[value]) //FIXME create a copy if needed - like value is a table
            } else {
                ctx.env.addLocal(identifier, value)
            }
        }
        return NilValue
    }
}


internal object EvaluateDo : Evaluate<DoStmt> {
    override fun invoke(ctx: EvaluationContext<DoStmt>): Value {
        ctx.enterScope()
        val result = ctx.evaluate { block }
        ctx.leaveScope()
        return result
    }
}

internal object EvaluateBlock : Evaluate<Block> {
    override fun invoke(ctx: EvaluationContext<Block>): Value {
        val (toEvaluate, env) = ctx
        var result: Value = NilValue
        for (statement in toEvaluate.statements) {
            result = ctx.evaluate(statement)
        }
        if (result is IdentValue) {
            return ctx.env[result]
        }
        return result
    }
}

internal object EvaluateCall : Evaluate<Call> {
    override fun invoke(ctx: EvaluationContext<Call>): Value {
        return ctx.evaluate { expression }
    }
}

internal object EvaluateExpressionStatement : Evaluate<ExpressionStatement> {
    override fun invoke(ctx: EvaluationContext<ExpressionStatement>): Value {
        return ctx.evaluate { expression }
    }
}

internal object EvaluatePrototype : Evaluate<Prototype> {
    override fun invoke(ctx: EvaluationContext<Prototype>): Value {
        val value = ctx.evaluateAsPrototype { expression }
        ctx.env.addLocal(value.identifier, value) //FIXME new env? or putting this in ctx as well like ctx.addLocal
        return value
    }
}

internal object EvaluateReturn : Evaluate<Return> {
    override fun invoke(ctx: EvaluationContext<Return>): Value {
        return ctx.evaluate { expression }
    }
}