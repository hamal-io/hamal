package io.hamal.script.interpreter

import io.hamal.script.ast.expr.*
import io.hamal.script.ast.stmt.*
import io.hamal.script.value.StringValue
import io.hamal.script.value.Value
import kotlin.reflect.KClass

internal object Evaluator {

    private val store = mutableMapOf<KClass<*>, Evaluate<*>>()

    init {
        /*LITERAL*/
        register(Identifier::class, EvaluateIdentifier)
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
        register(LiteralExpression::class, EvaluateLiteralExpression)
        register(PrefixExpression::class, EvaluatePrefixExpression)

        /*STATEMENT*/
        register(Statement::class, EvaluateStatement)
        register(Assignment.Global::class, EvaluateGlobalAssignment)
        register(Assignment.Local::class, EvaluateLocalAssignment)
        register(Block::class, EvaluateBlock)
        register(Call::class, EvaluateCall)
        register(ExpressionStatement::class, EvaluateExpressionStatement)
        register(Prototype::class, EvaluatePrototype)
        register(Return::class, EvaluateReturn)
    }

    fun <TYPE : Any> evaluate(toEvaluate: TYPE, env: Environment): Value {
        val evaluate = resolve(toEvaluate::class)
        return evaluate.invoke(toEvaluate, env)
    }

    fun <TYPE : Any> evaluateAsString(toEvaluate: TYPE, env: Environment): StringValue {
        val result = evaluate(toEvaluate, env)
        require(result is StringValue)
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
        override fun run(toEvaluate: Statement, env: Environment) = Evaluator.evaluate(toEvaluate, env)
    }
}

internal interface Evaluate<TYPE : Any> {
    operator fun invoke(toEvaluate: TYPE, env: Environment): Value
}
