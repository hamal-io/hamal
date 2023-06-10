package io.hamal.lib.script.impl.eval

import io.hamal.lib.script.api.value.*
import io.hamal.lib.script.impl.ast.expr.*


internal class EvaluateCallExpression<INVOKE_CTX : FuncInvocationContext> :
    Evaluate<CallExpression, INVOKE_CTX> {
    override fun invoke(ctx: EvaluationContext<CallExpression, INVOKE_CTX>): Value {
        val toEvaluate = ctx.toEvaluate
        val env = ctx.env

        val parameters = toEvaluate.parameters.map { ctx.evaluate(it) }

        val target = ctx.evaluate { ident }
        if (target is IdentValue) {
            val ident = ctx.evaluateAsIdentifier { ident }

            when (val func = env.find(ident)) {
                is BuiltinFuncValue -> {
                    return func(
                        BuiltinFuncValue.Context(
                            parameters = parameters.zip(toEvaluate.parameters)
                                .map { BuiltinFuncValue.Parameter(it.first, it.second) },
                            env = env
                        )
                    )
                }

                is PrototypeValue -> {
                    return ctx.evaluate(func.block)
                }

                else -> TODO()
            }
        } else {
            @Suppress("UNCHECKED_CAST")
            val func: FuncValue<INVOKE_CTX> = target as FuncValue<INVOKE_CTX>
            val invokeCtx: INVOKE_CTX = ctx.funcInvocationContextFactory.create(parameters, env)
            return func(invokeCtx)
        }
    }
}

internal class EvaluateGroupedExpression<INVOKE_CTX : FuncInvocationContext> :
    Evaluate<GroupedExpression, INVOKE_CTX> {
    override fun invoke(ctx: EvaluationContext<GroupedExpression, INVOKE_CTX>) = ctx.evaluate { expression }
}

internal class EvaluateInfixExpression<INVOKE_CTX : FuncInvocationContext> :
    Evaluate<InfixExpression, INVOKE_CTX> {
    override fun invoke(ctx: EvaluationContext<InfixExpression, INVOKE_CTX>): Value {
        val self = ctx.evaluate { lhs }
        val other = ctx.evaluate { rhs }
        return ctx.evaluateInfix(ctx.toEvaluate.operator, self, other)
    }
}

internal class EvaluatePrefixExpression<INVOKE_CTX : FuncInvocationContext> :
    Evaluate<PrefixExpression, INVOKE_CTX> {
    override fun invoke(ctx: EvaluationContext<PrefixExpression, INVOKE_CTX>): Value {
        val value = ctx.evaluate { expression }
        return ctx.evaluatePrefix(ctx.toEvaluate.operator, value)
    }

}

internal class EvaluateIfExpression<INVOKE_CTX : FuncInvocationContext> : Evaluate<IfExpression, INVOKE_CTX> {
    override fun invoke(ctx: EvaluationContext<IfExpression, INVOKE_CTX>): Value {
        for (conditionalStatement in ctx.toEvaluate.conditionalExpression) {
            return when (ctx.evaluate(conditionalStatement.condition)) {
                FalseValue -> continue
                TrueValue -> {
                    ctx.enterScope()
                    val result = ctx.evaluate(conditionalStatement.block)
                    ctx.leaveScope()
                    result
                }

                else -> ErrorValue("Expression expected to yield a boolean value")
            }
        }
        return NilValue
    }

}

internal class EvaluateForLoopExpression<INVOKE_CTX : FuncInvocationContext> :
    Evaluate<ForLoopExpression, INVOKE_CTX> {
    override fun invoke(ctx: EvaluationContext<ForLoopExpression, INVOKE_CTX>): Value {
        val ident = ctx.evaluateAsIdentifier { ident }
        var currentValue: NumberValue = ctx.evaluate { startExpression } as NumberValue

        val endValue = ctx.evaluate { endExpression } as NumberValue
        val stepValue = ctx.evaluate { stepExpression } as NumberValue

        val hasNext = if (stepValue.isGreaterThanEqual(NumberValue.Zero)) {
            HasNext.Forward
        } else {
            HasNext.Backwards
        }

        while (true) {
            ctx.env.addLocal(ident, currentValue)
            ctx.evaluate { block }
            val nextValue = currentValue.plus(stepValue)
            if (hasNext(nextValue, endValue)) {
                currentValue = nextValue
            } else {
                break
            }
        }
        return currentValue
    }

    private enum class HasNext {
        Forward {
            override fun invoke(nextValue: NumberValue, endValue: NumberValue): Boolean {
                return nextValue.isLessThanEqual(endValue)
            }
        },
        Backwards {
            override fun invoke(nextValue: NumberValue, endValue: NumberValue): Boolean {
                return nextValue.isGreaterThanEqual(endValue)
            }
        };

        abstract operator fun invoke(nextValue: NumberValue, endValue: NumberValue): Boolean
    }
}