package io.hamal.lib.script.impl.eval

import io.hamal.lib.script.api.ast.Statement
import io.hamal.lib.script.api.value.FuncInvocationContext
import io.hamal.lib.script.api.value.IdentValue
import io.hamal.lib.script.api.value.NilValue
import io.hamal.lib.script.api.value.Value
import io.hamal.lib.script.impl.ast.stmt.*

internal class EvaluateStatement<INVOKE_CTX : FuncInvocationContext> : Evaluate<Statement, INVOKE_CTX> {
    override fun invoke(ctx: EvaluationContext<Statement, INVOKE_CTX>): Value {
        return when (val toEvaluate = ctx.toEvaluate) {
            is ExpressionStatement -> ctx.evaluate(toEvaluate)
            else -> TODO("Evaluation of $toEvaluate not supported yet")
        }
    }
}

internal class EvaluateGlobalAssignment<INVOKE_CTX : FuncInvocationContext> :
    Evaluate<Assignment.Global, INVOKE_CTX> {
    override fun invoke(ctx: EvaluationContext<Assignment.Global, INVOKE_CTX>): Value {
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

internal class EvaluateLocalAssignment<INVOKE_CTX : FuncInvocationContext> :
    Evaluate<Assignment.Local, INVOKE_CTX> {
    override fun invoke(ctx: EvaluationContext<Assignment.Local, INVOKE_CTX>): Value {
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


internal class EvaluateDo<INVOKE_CTX : FuncInvocationContext> : Evaluate<DoStmt, INVOKE_CTX> {
    override fun invoke(ctx: EvaluationContext<DoStmt, INVOKE_CTX>): Value {
        ctx.enterScope()
        val result = ctx.evaluate { block }
        ctx.leaveScope()
        return result
    }
}

internal class EvaluateBlock<INVOKE_CTX : FuncInvocationContext> : Evaluate<Block, INVOKE_CTX> {
    override fun invoke(ctx: EvaluationContext<Block, INVOKE_CTX>): Value {
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

internal class EvaluateCall<INVOKE_CTX : FuncInvocationContext> : Evaluate<Call, INVOKE_CTX> {
    override fun invoke(ctx: EvaluationContext<Call, INVOKE_CTX>): Value {
        return ctx.evaluate { expression }
    }
}

internal class EvaluateExpressionStatement<INVOKE_CTX : FuncInvocationContext> :
    Evaluate<ExpressionStatement, INVOKE_CTX> {
    override fun invoke(ctx: EvaluationContext<ExpressionStatement, INVOKE_CTX>): Value {
        return ctx.evaluate { expression }
    }
}

internal class EvaluatePrototype<INVOKE_CTX : FuncInvocationContext> : Evaluate<Prototype, INVOKE_CTX> {
    override fun invoke(ctx: EvaluationContext<Prototype, INVOKE_CTX>): Value {
        val value = ctx.evaluateAsPrototype { expression }
        ctx.env.addLocal(value.ident, value) //FIXME new env? or putting this in ctx as well like ctx.addLocal
        return value
    }
}

internal class EvaluateReturn<INVOKE_CTX : FuncInvocationContext> : Evaluate<Return, INVOKE_CTX> {
    override fun invoke(ctx: EvaluationContext<Return, INVOKE_CTX>): Value {
        return ctx.evaluate { expression }
    }
}