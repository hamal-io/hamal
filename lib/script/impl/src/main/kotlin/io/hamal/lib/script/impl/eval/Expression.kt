package io.hamal.lib.script.impl.eval

import io.hamal.lib.script.api.value.*
import io.hamal.lib.script.impl.ast.expr.*

internal object EvaluateCallExpression : Evaluate<CallExpression> {
    override fun invoke(ctx: EvaluationContext<CallExpression>): Value {
        val toEvaluate = ctx.toEvaluate
        val env = ctx.env

        val parameters = toEvaluate.parameters.map { ctx.evaluate(it) }


        val target = ctx.evaluate { ident }

        if (target is IdentValue) {

            val ident = ctx.evaluateAsIdentifier { ident }

            val func = env.find(ident)

            when (func) {
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
//            require(target is DepFunctionValue)
//            return target(
//                Context(
//                    parameters.map {
//                        Parameter(
//                            value = it,
//                            expression = toEvaluate.parameters.first() //FIXME
//                        )
//                    },
//                    ctx.env
//                )
//            )
            TODO()
        }
    }
}

internal object EvaluateGroupedExpression : Evaluate<GroupedExpression> {
    override fun invoke(ctx: EvaluationContext<GroupedExpression>) = ctx.evaluate { expression }
}

internal object EvaluateInfixExpression : Evaluate<InfixExpression> {
    override fun invoke(ctx: EvaluationContext<InfixExpression>): Value {
        val self = ctx.evaluate { lhs }
        val other = ctx.evaluate { rhs }
        return ctx.evaluateInfix(ctx.toEvaluate.operator, self, other)
    }
}

internal object EvaluatePrefixExpression : Evaluate<PrefixExpression> {
    override fun invoke(ctx: EvaluationContext<PrefixExpression>): Value {
        val value = ctx.evaluate { expression }
        return ctx.evaluatePrefix(ctx.toEvaluate.operator, value)
    }

}

internal object EvaluateIfExpression : Evaluate<IfExpression> {
    override fun invoke(ctx: EvaluationContext<IfExpression>): Value {
        for (conditionalStatement in ctx.toEvaluate.conditionalExpression) {
            val conditionValue = ctx.evaluate(conditionalStatement.condition)
            return when (conditionValue) {
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

internal object EvaluateForLoopExpression : Evaluate<ForLoopExpression> {
    override fun invoke(ctx: EvaluationContext<ForLoopExpression>): Value {
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