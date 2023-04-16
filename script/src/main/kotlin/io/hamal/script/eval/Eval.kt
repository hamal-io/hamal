package io.hamal.script.eval

import io.hamal.script.ast.expr.*
import io.hamal.script.ast.expr.Operator.Minus
import io.hamal.script.ast.stmt.*
import io.hamal.script.value.*

interface Eval {

    operator fun invoke(statement: Statement, env: Environment): Value

    class DefaultImpl : Eval {
        override fun invoke(statement: Statement, env: Environment): Value {
            return when (statement) {
                is Assignment -> evalAssignment(statement, env)
                is Block -> evalBlockStatement(statement, env)
                is Call -> evalCallStatement(statement, env)
                is Prototype -> evalPrototype(statement, env)
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

private fun Eval.DefaultImpl.evalAssignment(assignment: Assignment, env: Environment): Value {
    val result = TableValue()
    //FIXME populate environment
    assignment.identifiers.zip(assignment.expressions)
        .forEach {
            result[StringValue(it.first)] = evalExpression(it.second, env)
        }
    return result
}

private fun Eval.DefaultImpl.evalCallStatement(call: Call, env: Environment) : Value{
    //FIXME the same as the expression ?!
    val prototype = env.findLocalPrototype(StringValue(call.identifier))!!
    return evalBlockStatement(prototype.block, env)
}

private fun Eval.DefaultImpl.evalPrototype(prototype: Prototype, env: Environment): Value{
    //FIXME this not local
    val value = evalExpression(prototype.expression, env) as PrototypeValue

    env.assignLocal(value.identifier, value)
    return value
}

private fun Eval.DefaultImpl.evalReturnStatement(returnStatement: Return, env: Environment): Value {
    return evalExpression(returnStatement.returnValue, env)
}

private fun Eval.DefaultImpl.evalExpression(expression: Expression, env: Environment): Value {
    return when (expression) {
        is PrefixExpression -> evalPrefix(expression, env)
        is InfixExpression -> evalInfix(expression, env)
        is LiteralExpression -> evalLiteral(expression, env)
        is GroupedExpression -> evalExpression(expression.expression, env)
        is CallExpression -> evalCallExpression(expression, env)
        else -> TODO("$expression not supported yet")
    }
}

private fun Eval.DefaultImpl.evalPrefix(expression: PrefixExpression, env: Environment): Value {
    val value = evalExpression(expression.value, env)
    return when (expression.operator) {
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

        Minus -> {
            NumberValue((lhs as NumberValue).value.minus((rhs as NumberValue).value))
        }

        else -> TODO()
    }
}

private fun evalLiteral(literal: LiteralExpression, env: Environment): Value {
    return when (literal) {
        is NilLiteral -> NilValue
        is TrueLiteral -> TrueValue
        is FalseLiteral -> FalseValue
        is NumberLiteral -> NumberValue(literal.value)
        is PrototypeLiteral -> PrototypeValue(
            evalIdentifier(literal.identifier),
            literal.parameters.map(::evalIdentifier),
            literal.block
        )

        else -> TODO()
    }
}

private fun evalIdentifier(identifier: Identifier) = StringValue(identifier.value)

private fun Eval.DefaultImpl.evalCallExpression(expression: CallExpression, env: Environment): Value {
    //FIXME the same as statement ?!
    val prototype = env.findLocalPrototype(StringValue(expression.identifier.value))!!
    return evalBlockStatement(prototype.block, env)
}