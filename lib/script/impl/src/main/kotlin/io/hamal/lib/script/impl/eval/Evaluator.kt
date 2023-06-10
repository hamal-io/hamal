package io.hamal.lib.script.impl.eval

import io.hamal.lib.script.api.ast.Node
import io.hamal.lib.script.api.ast.Statement
import io.hamal.lib.script.api.value.FuncInvocationContext
import io.hamal.lib.script.api.value.Value
import io.hamal.lib.script.impl.ast.expr.*
import io.hamal.lib.script.impl.ast.stmt.*
import kotlin.reflect.KClass

internal interface Evaluate<TYPE : Node, INVOKE_CTX : FuncInvocationContext> {
    operator fun invoke(ctx: EvaluationContext<TYPE, INVOKE_CTX>): Value
}


internal interface Evaluator<INVOKE_CTX : FuncInvocationContext> {
    fun <TYPE : Node> evaluate(ctx: EvaluationContext<TYPE, INVOKE_CTX>): Value
}

internal class DefaultEvaluator<INVOKE_CTX : FuncInvocationContext> : Evaluator<INVOKE_CTX> {
    override fun <TYPE : Node> evaluate(ctx: EvaluationContext<TYPE, INVOKE_CTX>): Value {
        val evaluate = resolve(ctx.toEvaluate::class)
        return evaluate.invoke(ctx)
    }

    private val store = mutableMapOf<KClass<*>, Evaluate<*, INVOKE_CTX>>()

    init {
        /*LITERAL*/
        register(IdentifierLiteral::class, EvaluateIdentifier())
        register(NumberLiteral::class, EvaluateNumberLiteral())
        register(StringLiteral::class, EvaluateStringLiteral())
        register(CodeLiteral::class, EvaluateCodeLiteral())
        register(FalseLiteral::class, EvaluateFalseLiteral())
        register(TrueLiteral::class, EvaluateTrueLiteral())
        register(NilLiteral::class, EvaluateNilLiteral())
        register(PrototypeLiteral::class, EvaluatePrototypeLiteral())

        /*EXPRESSION*/
        register(CallExpression::class, EvaluateCallExpression())
        register(GroupedExpression::class, EvaluateGroupedExpression())
        register(InfixExpression::class, EvaluateInfixExpression())
        register(PrefixExpression::class, EvaluatePrefixExpression())
        register(TableAccessExpression::class, EvaluateTableAccess())
        register(TableConstructorExpression::class, EvaluateTableConstructor())
        register(IfExpression::class, EvaluateIfExpression())
        register(ForLoopExpression::class, EvaluateForLoopExpression())


        /*STATEMENT*/
        register(Statement::class, EvaluateStatement())
        register(Assignment.Global::class, EvaluateGlobalAssignment())
        register(Assignment.Local::class, EvaluateLocalAssignment())
        register(Block::class, EvaluateBlock())
        register(Call::class, EvaluateCall())
        register(DoStmt::class, EvaluateDo())
        register(ExpressionStatement::class, EvaluateExpressionStatement())
        register(Prototype::class, EvaluatePrototype())
        register(Return::class, EvaluateReturn())
    }

    private fun <TYPE : Node> resolve(targetClass: KClass<out TYPE>): Evaluate<TYPE, INVOKE_CTX> {
        @Suppress("UNCHECKED_CAST")
        return store[targetClass]!! as Evaluate<TYPE, INVOKE_CTX>
    }

    private fun <TYPE : Node> register(targetClazz: KClass<TYPE>, evaluate: Evaluate<TYPE, INVOKE_CTX>) {
        store[targetClazz] = evaluate
    }
}