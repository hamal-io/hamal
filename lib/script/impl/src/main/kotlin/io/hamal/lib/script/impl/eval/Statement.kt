package io.hamal.lib.script.impl.eval

import io.hamal.lib.script.api.ast.Statement
import io.hamal.lib.script.api.value.FuncContext
import io.hamal.lib.script.api.value.IdentValue
import io.hamal.lib.script.api.value.NilValue
import io.hamal.lib.script.api.value.Value
import io.hamal.lib.script.impl.ast.stmt.*

internal class EvaluateStatement : Evaluate<Statement> {
    override fun invoke(ctx: EvaluationContext<Statement>): Value {
        return when (val toEvaluate = ctx.toEvaluate) {
            is ExpressionStatement -> ctx.evaluate(toEvaluate)
            else -> TODO("Evaluation of $toEvaluate not supported yet")
        }
    }
}

internal class EvaluateGlobalAssignment :
    Evaluate<Assignment.Global> {
    override fun invoke(ctx: EvaluationContext<Assignment.Global>): Value {
        val identifiers = ctx.toEvaluate.identifiers.map(ctx::evaluateAsIdentifier)
        val values = ctx.toEvaluate.expressions.map(ctx::evaluate)
        identifiers.zip(values).forEach { (ident, value) ->
            if (value is IdentValue) {
                ctx.env.addGlobal(ident, ctx.env[value]) //FIXME create a copy if needed - like value is a table
            } else {
                ctx.env.addGlobal(ident, value)
            }
        }
        return NilValue
    }
}

internal class EvaluateLocalAssignment :
    Evaluate<Assignment.Local> {
    override fun invoke(ctx: EvaluationContext<Assignment.Local>): Value {
        val identifiers = ctx.toEvaluate.identifiers.map(ctx::evaluateAsIdentifier)
        val values = ctx.toEvaluate.expressions.map(ctx::evaluate)
        identifiers.zip(values).forEach { (ident, value) ->
            if (value is IdentValue) {
                ctx.env.addLocal(ident, ctx.env[value]) //FIXME create a copy if needed - like value is a table
            } else {
                ctx.env.addLocal(ident, value)
            }
        }
        return NilValue
    }
}


internal class EvaluateDo : Evaluate<DoStmt> {
    override fun invoke(ctx: EvaluationContext<DoStmt>): Value {
        ctx.enterScope()
        val result = ctx.evaluate { block }
        ctx.leaveScope()
        return result
    }
}

internal class EvaluateBlock : Evaluate<Block> {
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

internal class EvaluateCall : Evaluate<Call> {
    override fun invoke(ctx: EvaluationContext<Call>): Value {
        return ctx.evaluate { expression }
    }
}

internal class EvaluateExpressionStatement :
    Evaluate<ExpressionStatement> {
    override fun invoke(ctx: EvaluationContext<ExpressionStatement>): Value {
        return ctx.evaluate { expression }
    }
}

internal class EvaluatePrototype : Evaluate<Prototype> {
    override fun invoke(ctx: EvaluationContext<Prototype>): Value {
        val value = ctx.evaluateAsPrototype { expression }
        ctx.env.addLocal(value.ident, value) //FIXME new env? or putting this in ctx as well like ctx.addLocal
        return value
    }
}

internal class EvaluateReturn : Evaluate<Return> {
    override fun invoke(ctx: EvaluationContext<Return>): Value {
        return ctx.evaluate { expression }
    }
}