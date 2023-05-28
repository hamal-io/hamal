package io.hamal.lib.script.impl.eval

import io.hamal.lib.script.api.ast.Node
import io.hamal.lib.script.api.ast.Statement
import io.hamal.lib.script.api.value.DepValue
import io.hamal.lib.script.impl.ast.expr.*
import io.hamal.lib.script.impl.ast.stmt.*
import kotlin.reflect.KClass

internal interface Evaluate<TYPE : Node> {
    operator fun invoke(ctx: EvaluationContext<TYPE>): DepValue
}


internal interface Evaluator {
    fun <TYPE : Node> evaluate(ctx: EvaluationContext<TYPE>): DepValue
}

internal class DefaultEvaluator : Evaluator {
    override fun <TYPE : Node> evaluate(ctx: EvaluationContext<TYPE>): DepValue {
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
        register(TableAccessExpression::class, EvaluateTableAccess)
        register(TableConstructorExpression::class, EvaluateTableConstructor)
        register(IfExpression::class, EvaluateIfExpression)
        register(ForLoopExpression::class, EvaluateForLoopExpression)


        /*STATEMENT*/
        register(Statement::class, EvaluateStatement)
        register(Assignment.Global::class, EvaluateGlobalAssignment)
        register(Assignment.Local::class, EvaluateLocalAssignment)
        register(Block::class, EvaluateBlock)
        register(Call::class, EvaluateCall)
        register(DoStmt::class, EvaluateDo)
        register(ExpressionStatement::class, EvaluateExpressionStatement)
        register(Prototype::class, EvaluatePrototype)
        register(Return::class, EvaluateReturn)
    }

    private fun <TYPE : Node> resolve(targetClass: KClass<out TYPE>): Evaluate<TYPE> {
        @Suppress("UNCHECKED_CAST")
        return store[targetClass]!! as Evaluate<TYPE>
    }

    private fun <TYPE : Node> register(targetClazz: KClass<TYPE>, evaluate: Evaluate<TYPE>) {
        store[targetClazz] = evaluate
    }
}