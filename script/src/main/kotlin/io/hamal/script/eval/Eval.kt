package io.hamal.script.eval

import io.hamal.script.ast.Expression
import io.hamal.script.ast.ExpressionStatement
import io.hamal.script.ast.Statement
import io.hamal.script.ast.expr.*
import io.hamal.script.ast.expr.Number
import io.hamal.script.ast.expr.Operator.Minus
import io.hamal.script.ast.stmt.Block
import io.hamal.script.ast.stmt.Return
import io.hamal.script.value.*

interface Eval {

    operator fun invoke(statement: Statement, env: Environment): Value

    class DefaultImpl : Eval {
        override fun invoke(statement: Statement, env: Environment): Value {
            return when (statement) {
                is Block -> evalBlockStatement(statement, env)
                is Return -> evalReturnStatement(statement, env)
                is ExpressionStatement -> evalExpression(statement.expression, env)
                else -> TODO()
            }
        }
    }
}

private fun Eval.DefaultImpl.evalBlockStatement(blockStatement: Block, env: Environment): Value {
    var result: Value = NilValue
    for (statement in blockStatement.statements) {
        result = invoke(statement, env)
    }
    return result
}

private fun Eval.DefaultImpl.evalReturnStatement(returnStatement: Return, env: Environment): Value {
    return evalExpression(returnStatement.returnValue, env)
}

private fun Eval.DefaultImpl.evalExpression(expression: Expression, env: Environment): Value {
    return when (expression) {
        is PrefixExpression -> evalPrefix(expression, env)
        is InfixExpression -> evalInfix(expression, env)
        is LiteralExpression -> evalLiteral(expression)
        is GroupedExpression -> evalExpression(expression.expression, env)
        is CallExpression -> evalCallExpression(expression,env)
        else -> TODO("$expression not supported yet")
    }
}

private fun Eval.DefaultImpl.evalPrefix(expression: PrefixExpression, env: Environment) : Value{
    val value = evalExpression(expression.value,env)
    return when(expression.operator){
        // FIXME this must come from operator repository as well
        Minus -> NumberValue((value as NumberValue).value.negate())
        else -> TODO("${expression.operator} not supported")
    }
}

private fun Eval.DefaultImpl.evalInfix(expression: InfixExpression, env: Environment): Value {
    //FIXME error handling
    val lhs = evalExpression(expression.lhs, env)
    //FIXME error handling
    val rhs = evalExpression(expression.rhs, env)

    return eval(expression.operator, lhs, rhs, env)
}


private fun eval(operator: Operator, lhs: Value, rhs: Value, env: Environment): Value {
    //FIXME have an operator registry where operations of operators are specified based on the input --
    // should be a nice isolation for tests
    return when (operator) {
        Operator.Plus -> {
            NumberValue((lhs as NumberValue).value.plus((rhs as NumberValue).value))
        }
        Minus ->{
            NumberValue((lhs as NumberValue).value.minus((rhs as NumberValue).value))
        }
        else -> TODO()
    }
}

private fun evalLiteral(literal: LiteralExpression): Value {
    return when (literal) {
        is Nil -> NilValue
        is True -> TrueValue
        is False -> FalseValue
        is Number -> NumberValue(literal.value)
        is Prototype -> PrototypeValue(
            evalIdentifier( literal.identifier),
            literal.parameters.map(::evalIdentifier),
            literal.block
        )
        else -> TODO()
    }
}

private fun evalIdentifier(identifier: Identifier) = StringValue(identifier.value)

private fun Eval.DefaultImpl.evalCallExpression(expression: CallExpression, env: Environment) : Value {
    val fn = evalExpression(expression.prototype, env) as PrototypeValue
    return evalBlockStatement(fn.block, env)
}