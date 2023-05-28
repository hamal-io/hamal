package io.hamal.lib.script.impl.eval

import io.hamal.lib.script.api.Context
import io.hamal.lib.script.api.Parameter
import io.hamal.lib.script.api.value.*
import io.hamal.lib.script.impl.ast.expr.*

internal object EvaluateCallExpression : Evaluate<CallExpression> {
    override fun invoke(ctx: EvaluationContext<CallExpression>): DepValue {
        val toEvaluate = ctx.toEvaluate
        val env = ctx.env

        val parameters = toEvaluate.parameters.map { ctx.evaluate(it) }


        val target = ctx.evaluate { identifier }

        if (target is DepIdentifier) {

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
            require(target is DepFunctionValue)
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
    override fun invoke(ctx: EvaluationContext<InfixExpression>): DepValue {
        val self = ctx.evaluate { lhs }
        val other = ctx.evaluate { rhs }
        return ctx.evaluateInfix(ctx.toEvaluate.operator, self, other)
    }
}

internal object EvaluatePrefixExpression : Evaluate<PrefixExpression> {
    override fun invoke(ctx: EvaluationContext<PrefixExpression>): DepValue {
        val value = ctx.evaluate { expression }
        return ctx.evaluatePrefix(ctx.toEvaluate.operator, value)
    }

}

internal object EvaluateIfExpression : Evaluate<IfExpression> {
    override fun invoke(ctx: EvaluationContext<IfExpression>): DepValue {
        for (conditionalStatement in ctx.toEvaluate.conditionalExpression) {
            val conditionValue = ctx.evaluate(conditionalStatement.condition)
            return when (conditionValue) {
                DepFalseValue -> continue
                DepTrueValue -> {
                    ctx.enterScope()
                    val result = ctx.evaluate(conditionalStatement.block)
                    ctx.leaveScope()
                    result
                }

                else -> DepErrorValue("Expression expected to yield a boolean value")
            }
        }
        return DepNilValue
    }

}

internal object EvaluateForLoopExpression : Evaluate<ForLoopExpression> {
    override fun invoke(ctx: EvaluationContext<ForLoopExpression>): DepValue {
        val identifier = ctx.evaluateAsIdentifier { identifier }
        var currentValue: DepNumberValue = ctx.evaluate { startExpression } as DepNumberValue

        val endValue = ctx.evaluate { endExpression } as DepNumberValue
        val stepValue = ctx.evaluate { stepExpression } as DepNumberValue

        val hasNext = if (stepValue.isGreaterThanEqual(DepNumberValue.Zero)) {
            HasNext.Forward
        } else {
            HasNext.Backwards
        }

        while (true) {
            ctx.env.addLocal(identifier, currentValue)
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
            override fun invoke(nextValue: DepNumberValue, endValue: DepNumberValue): Boolean {
                return nextValue.isLessThanEqual(endValue)
            }
        },
        Backwards {
            override fun invoke(nextValue: DepNumberValue, endValue: DepNumberValue): Boolean {
                return nextValue.isGreaterThanEqual(endValue)
            }
        };

        abstract operator fun invoke(nextValue: DepNumberValue, endValue: DepNumberValue): Boolean
    }
}