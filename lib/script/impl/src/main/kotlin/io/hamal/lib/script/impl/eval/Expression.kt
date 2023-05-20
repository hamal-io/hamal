package io.hamal.lib.script.impl.eval

import io.hamal.lib.common.math.Decimal
import io.hamal.lib.script.api.Context
import io.hamal.lib.script.api.Parameter
import io.hamal.lib.script.api.value.*
import io.hamal.lib.script.impl.ast.expr.*

internal object EvaluateCallExpression : Evaluate<CallExpression> {
    override fun invoke(ctx: EvaluationContext<CallExpression>): Value {
        val toEvaluate = ctx.toEvaluate
        val env = ctx.env

        val parameters = toEvaluate.parameters.map { ctx.evaluate(it) }


        val target = ctx.evaluate { identifier }

        if (target is Identifier) {

            val identifier = ctx.evaluateAsIdentifier { identifier }
            env.findFunctionValue(identifier)
                ?.let { fn ->
                    return fn(
                        Context(
                            parameters = parameters.zip(toEvaluate.parameters)
                                .map { Parameter(it.first, it.second) },
                            env = env
                        )
                    )
                }

            val prototype = env.findProtoTypeValue(identifier)!!
            return ctx.evaluate(prototype.block)
        } else {
            require(target is FunctionValue)
            return target(
                Context(
                    parameters.map {
                        Parameter(
                            value = it,
                            expression = toEvaluate.parameters.first() //FIXME
                        )
                    },
                    ctx.env
                )
            )
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
                TrueValue -> ctx.evaluate(conditionalStatement.block)
                else -> ErrorValue("Expression expected to yield a boolean value")
            }
        }
        return NilValue
    }

}

internal object EvaluateForLoopExpression : Evaluate<ForLoopExpression> {
    override fun invoke(ctx: EvaluationContext<ForLoopExpression>): Value {
        val identifier = ctx.evaluateAsIdentifier { identifier }
        var currentValue: NumberValue = ctx.evaluate { startExpression } as NumberValue

        val endValue = ctx.evaluate { endExpression } as NumberValue
        val stepValue = ctx.evaluate { stepExpression } as NumberValue

        val hasNext = if (stepValue.value.isGreaterThanEqual(Decimal.Zero)) {
            HasNext.Forward
        } else {
            HasNext.Backwards
        }

        while (true) {
            ctx.env.addLocal(identifier, currentValue)
            ctx.evaluate { block }
            val nextValue = NumberValue(currentValue.value.plus(stepValue.value))
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
                return nextValue.value.isLessThanEqual(endValue.value)
            }
        },
        Backwards {
            override fun invoke(nextValue: NumberValue, endValue: NumberValue): Boolean {
                return nextValue.value.isGreaterThanEqual(endValue.value)
            }
        };

        abstract operator fun invoke(nextValue: NumberValue, endValue: NumberValue): Boolean
    }
}