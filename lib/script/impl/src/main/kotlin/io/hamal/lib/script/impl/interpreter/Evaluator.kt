package io.hamal.lib.script.impl.interpreter

import io.hamal.lib.script.api.Environment
import io.hamal.lib.script.api.ast.Node
import io.hamal.lib.script.api.value.Identifier
import io.hamal.lib.script.api.value.StringValue
import io.hamal.lib.script.api.value.Value
import io.hamal.lib.script.impl.ast.expr.*
import io.hamal.lib.script.impl.ast.stmt.*
import io.hamal.lib.script.impl.value.PrototypeValue
import kotlin.reflect.KClass

internal interface Evaluate<TYPE : Node> {
    operator fun invoke(ctx: EvaluationContext<TYPE>): Value
}

internal data class EvaluationContext<TYPE : Node>(
    val toEvaluate: TYPE,
    val env: Environment,
    val evaluator: Evaluator
) {

    fun <NEW_TYPE : Node> evaluate(
        env: Environment = this.env,
        block: TYPE.() -> NEW_TYPE
    ): Value {
        return evaluate(block(toEvaluate), env)
    }

    fun <TYPE : Node> evaluate(
        toEvaluate: TYPE,
        env: Environment = this.env
    ): Value {
        return evaluator.evaluate(
            EvaluationContext(toEvaluate, env, evaluator)
        )
    }

    fun <NEW_TYPE : Node> evaluateAsIdentifier(
        env: Environment = this.env,
        block: TYPE.() -> NEW_TYPE
    ): Identifier {
        return evaluateAsIdentifier(block(toEvaluate), env)
    }

    fun <TYPE : Node> evaluateAsIdentifier(
        toEvaluate: TYPE,
        env: Environment = this.env
    ): Identifier {
        val result = evaluate(toEvaluate, env)
        require(result is Identifier)
        return result
    }

    fun <NEW_TYPE : Node> evaluateAsPrototype(
        env: Environment = this.env,
        block: TYPE.() -> NEW_TYPE
    ): PrototypeValue {
        return evaluateAsPrototype(block(toEvaluate), env)
    }

    fun <TYPE : Node> evaluateAsPrototype(
        toEvaluate: TYPE,
        env: Environment = this.env
    ): PrototypeValue {
        val result = evaluate(toEvaluate, env)
        require(result is PrototypeValue)
        return result
    }

    fun <NEW_TYPE : Node> evaluateAsString(
        env: Environment = this.env,
        block: TYPE.() -> NEW_TYPE
    ): StringValue {
        return evaluateAsString(block(toEvaluate), env)
    }

    fun <TYPE : Node> evaluateAsString(
        toEvaluate: TYPE,
        env: Environment = this.env
    ): StringValue {
        val result = evaluate(toEvaluate, env)
        require(result is StringValue)
        return result
    }
}

internal interface Evaluator {
    fun <TYPE : Node> evaluate(ctx: EvaluationContext<TYPE>): Value
}

internal class DefaultEvaluator : Evaluator {
    override fun <TYPE : Node> evaluate(ctx: EvaluationContext<TYPE>): Value {
        val evaluate = resolve(ctx.toEvaluate::class)
        return evaluate.invoke(ctx)
    }

    private val store = mutableMapOf<KClass<*>, Evaluate<*>>()

    init {
        /*LITERAL*/
        register(IdentifierLiteral::class, EvaluateIdentifier)
        register(NumberLiteral::class, EvaluateNumberLiteral)
        register(StringLiteral::class, EvaluateStringLiteral)
        register(FalseLiteral::class, EvaluateFalseLiteral)
        register(TrueLiteral::class, EvaluateTrueLiteral)
        register(NilLiteral::class, EvaluateNilLiteral)
        register(PrototypeLiteral::class, EvaluatePrototypeLiteral)

        /*EXPRESSION*/
        register(CallExpression::class, EvaluateCallExpression)
        register(GroupedExpression::class, EvaluateGroupedExpression)
        register(InfixExpression::class, EvaluateInfixExpression)
        register(PrefixExpression::class, EvaluatePrefixExpression)

        /*STATEMENT*/
        register(Statement::class, EvaluateStatement)
        register(Assignment.Global::class, EvaluateGlobalAssignment)
        register(Assignment.Local::class, EvaluateLocalAssignment)
        register(BlockStatement::class, EvaluateBlock)
        register(Call::class, EvaluateCall)
        register(ExpressionStatement::class, EvaluateExpressionStatement)
        register(Prototype::class, EvaluatePrototype)
        register(Return::class, EvaluateReturn)
    }

    private fun <TYPE : Node> resolve(targetClass: KClass<out TYPE>): Evaluate<TYPE> {
        @Suppress("UNCHECKED_CAST")
        return store[targetClass] as Evaluate<TYPE>
    }

    private fun <TYPE : Node> register(targetClazz: KClass<TYPE>, evaluate: Evaluate<TYPE>) {
        store[targetClazz] = evaluate
    }
}