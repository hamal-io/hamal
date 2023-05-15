package io.hamal.lib.script.impl.interpreter

import io.hamal.lib.script.api.Environment
import io.hamal.lib.script.api.value.Identifier
import io.hamal.lib.script.api.value.StringValue
import io.hamal.lib.script.api.value.Value
import io.hamal.lib.script.impl.ast.expr.*
import io.hamal.lib.script.impl.ast.stmt.*
import kotlin.reflect.KClass

internal object Evaluator {

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
//        register(LiteralExpression::class, EvaluateLiteralExpression)
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

    fun <TYPE : Any> evaluate(ctx: EvaluationContext<TYPE>): Value {
        val evaluate = resolve(ctx.toEvaluate::class)
        return evaluate.invoke(ctx)
    }

    fun <TYPE : Any> evaluateAsString(ctx: EvaluationContext<TYPE>): StringValue {
        val result = evaluate(ctx)
        require(result is StringValue)
        return result
    }

    fun <TYPE : Any> evaluateAsIdentifier(ctx: EvaluationContext<TYPE>): Identifier {
        val result = evaluate(ctx)
        require(result is Identifier)
        return result
    }

    private fun <TYPE : Any> resolve(targetClass: KClass<out TYPE>): Evaluate<TYPE> {
        @Suppress("UNCHECKED_CAST")
        return store[targetClass] as Evaluate<TYPE>
    }

    private fun <TYPE : Any> register(targetClazz: KClass<TYPE>, evaluate: Evaluate<TYPE>) {
        store[targetClazz] = evaluate
    }
}


interface Interpreter {
    fun run(toEvaluate: Statement, env: Environment): Value

    object DefaultImpl : Interpreter {
        override fun run(toEvaluate: Statement, env: Environment) =
            Evaluator.evaluate(EvaluationContext(toEvaluate, env))
    }
}

internal interface Evaluate<TYPE : Any> {
    operator fun invoke(ctx: EvaluationContext<TYPE>): Value
}

data class EvaluationContext<TYPE : Any>(
    val toEvaluate: TYPE,
    val env: Environment
)
