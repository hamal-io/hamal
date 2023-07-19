package io.hamal.lib.script.impl.eval

import io.hamal.lib.script.api.ast.Node
import io.hamal.lib.kua.value.*
import io.hamal.lib.kua.value.ValueOperator.Type.*
import io.hamal.lib.script.impl.ast.expr.Operator

internal data class EvaluationContext<TYPE : Node>(
    val toEvaluate: TYPE,
    var env: EnvValue,
    val evaluator: Evaluator
) {

    fun enterScope() {
        env = env.enterScope()
    }

    fun leaveScope() {
        env = env.leaveScope()
    }


    fun <NEW_TYPE : Node> evaluate(
        env: EnvValue = this.env,
        block: TYPE.() -> NEW_TYPE
    ): Value {
        return evaluate(block(toEvaluate), env)
    }

    fun <TYPE : Node> evaluate(
        toEvaluate: TYPE,
        env: EnvValue = this.env
    ): Value {
        return evaluator.evaluate(
            EvaluationContext(toEvaluate, env, evaluator)
        )
    }

    fun <NEW_TYPE : Node> evaluateAsIdentifier(
        env: EnvValue = this.env,
        block: TYPE.() -> NEW_TYPE
    ): IdentValue {
        return evaluateAsIdentifier(block(toEvaluate), env)
    }

    fun <TYPE : Node> evaluateAsIdentifier(
        toEvaluate: TYPE,
        env: EnvValue = this.env
    ): IdentValue {
        val result = evaluate(toEvaluate, env)
        require(result is IdentValue)
        return result
    }

    fun <NEW_TYPE : Node> evaluateAsPrototype(
        env: EnvValue = this.env,
        block: TYPE.() -> NEW_TYPE
    ): PrototypeValue {
        return evaluateAsPrototype(block(toEvaluate), env)
    }

    fun <TYPE : Node> evaluateAsPrototype(
        toEvaluate: TYPE,
        env: EnvValue = this.env
    ): PrototypeValue {
        val result = evaluate(toEvaluate, env)
        require(result is PrototypeValue)
        return result
    }

    fun <NEW_TYPE : Node> evaluateAsString(
        env: EnvValue = this.env,
        block: TYPE.() -> NEW_TYPE
    ): StringValue {
        return evaluateAsString(block(toEvaluate), env)
    }

    fun <TYPE : Node> evaluateAsString(
        toEvaluate: TYPE,
        env: EnvValue = this.env
    ): StringValue {
        val result = evaluate(toEvaluate, env)
        require(result is StringValue)
        return result
    }

    fun <SELF : Value, OTHER : Value> evaluateInfix(
        operator: Operator,
        self: SELF,
        other: OTHER
    ): Value {
        val operationType = resolveInfixOperationType(operator)

        val selfValue = if (self is IdentValue) {
            env[self]
        } else {
            self
        }

        val otherValue = if (other is IdentValue) {
            env[other]
        } else {
            other
        }

        val operation = requireNotNull(selfValue.findInfixOperation(operationType, otherValue.type())) {
            "No infix operation specified for: ${selfValue.type()} $operator ${otherValue.type()}"
        }

        return operation(selfValue, otherValue)
    }

    fun <SELF : Value> evaluatePrefix(
        operator: Operator,
        self: SELF,
    ): Value {
        val operationType = resolvePrefixOperationType(operator)
        val operation = requireNotNull(self.findPrefixOperation(operationType)) {
            "No prefix operation specified for: $operationType ${self.type()}"
        }
        return operation(self)
    }
}

private fun resolveInfixOperationType(operator: Operator): ValueOperator.Type {
    return when {
        operator == Operator.Divide -> Div
        operator == Operator.Equals -> Eq
        operator == Operator.Exponential -> Pow
        operator == Operator.GreaterThan -> Gt
        operator == Operator.GreaterThanEquals -> Gte
        operator == Operator.LessThan -> Lt
        operator == Operator.LessThanEquals -> Lte
        operator == Operator.Minus -> Sub
        operator == Operator.Modulo -> Mod
        operator == Operator.Multiply -> Mul
        operator == Operator.NotEqual -> Neq
        operator == Operator.Plus -> Add
        else -> TODO()
    }
}

private fun resolvePrefixOperationType(operator: Operator): ValueOperator.Type {
    return when {
        operator == Operator.Minus -> Negate
        else -> TODO()
    }
}