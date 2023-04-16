package io.hamal.script.eval

import io.hamal.script.ScriptEvaluationException
import io.hamal.script.ast.expr.*
import io.hamal.script.ast.expr.Operator.Minus
import io.hamal.script.ast.stmt.*
import io.hamal.script.value.*

interface EvaluateExpression{
    operator fun invoke(toEvaluate: Expression, env: Environment): Value

    class DefaultImpl(
        var evaluateBlockStatement: EvaluateBlockStatement?
    ) : EvaluateExpression{
        override fun invoke(toEvaluate: Expression, env: Environment): Value {
                return when (toEvaluate) {
                    is PrefixExpression -> evalPrefix(toEvaluate, env)
                    is InfixExpression -> evalInfix(toEvaluate, env)
                    is LiteralExpression -> evalLiteral(toEvaluate, env)
                    is GroupedExpression -> evalExpression(toEvaluate.expression, env)
                    is CallExpression -> evalCallExpression(toEvaluate, env)
                    else -> TODO("$toEvaluate not supported yet")
            }
        }


        private fun evalExpression(expression: Expression, env: Environment): Value {
            return when (expression) {
                is PrefixExpression -> evalPrefix(expression, env)
                is InfixExpression -> evalInfix(expression, env)
                is LiteralExpression -> evalLiteral(expression, env)
                is GroupedExpression -> evalExpression(expression.expression, env)
                is CallExpression -> evalCallExpression(expression, env)
                else -> TODO("$expression not supported yet")
            }
        }

        private fun evalPrefix(expression: PrefixExpression, env: Environment): Value {
            val value = evalExpression(expression.value, env)
            return when (expression.operator) {
                // FIXME this must come from operator repository as well
                Minus -> NumberValue((value as NumberValue).value.negate())
                else -> TODO("${expression.operator} not supported")
            }
        }

        private fun evalInfix(expression: InfixExpression, env: Environment): Value {
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

        private fun evalCallExpression(expression: CallExpression, env: Environment): Value {
            //FIXME the same as statement ?!
            val prototype = env.findLocalPrototype(StringValue(expression.identifier.value))!!
            return evaluateBlockStatement!!(prototype.block, env)
            TODO()
        }
    }

}

interface EvaluateBlockStatement {
    operator fun invoke(toEvaluate: Block, env: Environment): Value

    class DefaultImpl(
        var evaluateStatement: EvaluateStatement?
    ): EvaluateBlockStatement {
        override fun invoke(toEvaluate: Block, env: Environment): Value {
            var result: Value = NilValue
            for (statement in toEvaluate.statements) {
                result = evaluateStatement!!(statement, env)
            }
            return result
        }

    }
}

interface EvaluateStatement{

    operator fun invoke(statement: Statement, env: Environment): Value

    class DefaultImpl(
        var evaluateExpression: EvaluateExpression?,
        var evaluateBlockStatement: EvaluateBlockStatement?
    ) : EvaluateStatement{
        override fun invoke(statement: Statement, env: Environment): Value {
            return when (statement) {
                is Assignment -> evalAssignment(statement, env)
                is Block -> evaluateBlockStatement!!(statement, env)
                is Call -> evalCallStatement(statement, env)
                is Prototype -> evalPrototype(statement, env)
                is Return -> evalReturnStatement(statement, env)
                is ExpressionStatement -> evaluateExpression!!(statement.expression, env)
                else -> TODO()
            }
        }


        private fun evalAssignment(assignment: Assignment, env: Environment): Value {
            val result = TableValue()
            //FIXME populate environment
            assignment.identifiers.zip(assignment.expressions)
                .forEach {
                    result[StringValue(it.first)] = evaluateExpression!!(it.second, env)
                }
            return result
        }

        fun assert(expressions: List<Expression>, parameters: List<Value>) : Value{
            val result = parameters.first()
//    val message = parameters.getOrNull(1) as StringValue? ?: StringValue("assertion violation")
            val message = StringValue("Assertion violated: '${expressions[0].toString()}'")
            if(result != TrueValue){
                throw ScriptEvaluationException(ErrorValue(message))
            }
            return NilValue
        }

        private fun evalCallStatement(call: Call, env: Environment) : Value {
            val parameters = call.parameters.map{evaluateExpression!!(it, env)}
            if(call.identifier.value == "assert"){
                return assert(call.parameters, parameters)
            }
            //FIXME the same as the expression ?!
            val prototype = env.findLocalPrototype(StringValue(call.identifier))!!
            return evaluateBlockStatement!!(prototype.block, env)
        }

        private fun evalPrototype(prototype: Prototype, env: Environment): Value{
            //FIXME this not local
            val value = evaluateExpression!!(prototype.expression, env) as PrototypeValue

            env.assignLocal(value.identifier, value)
            return value
        }

        private fun evalReturnStatement(returnStatement: Return, env: Environment): Value {
            return evaluateExpression!!(returnStatement.returnValue, env)
        }


    }
}

interface Eval {

    operator fun invoke(statement: Statement, env: Environment): Value

    class DefaultImpl : Eval {
        override fun invoke(statement: Statement, env: Environment): Value {
            val evaluateExpression = EvaluateExpression.DefaultImpl(null)
            val evaluateStatement = EvaluateStatement.DefaultImpl(evaluateExpression, null)
            val evaluateBlockStatement = EvaluateBlockStatement.DefaultImpl(evaluateStatement)
            evaluateExpression.evaluateBlockStatement = evaluateBlockStatement
            evaluateStatement.evaluateBlockStatement = evaluateBlockStatement

            return evaluateStatement(statement, env)
        }

    }
}
