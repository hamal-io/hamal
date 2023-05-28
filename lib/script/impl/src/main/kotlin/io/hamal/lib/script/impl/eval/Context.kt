package io.hamal.lib.script.impl.eval

import io.hamal.lib.script.api.ast.Node
import io.hamal.lib.script.api.value.*
import io.hamal.lib.script.api.value.DepValueOperation.Type.*
import io.hamal.lib.script.impl.ast.expr.Operator

internal data class EvaluationContext<TYPE : Node>(
    val toEvaluate: TYPE,
    var env: DepEnvironmentValue,
    val evaluator: Evaluator
) {

    fun enterScope() {
        env = env.enterScope()
    }

    fun leaveScope() {
        env = env.leaveScope()
    }


    fun <NEW_TYPE : Node> evaluate(
        env: DepEnvironmentValue = this.env,
        block: TYPE.() -> NEW_TYPE
    ): DepValue {
        return evaluate(block(toEvaluate), env)
    }

    fun <TYPE : Node> evaluate(
        toEvaluate: TYPE,
        env: DepEnvironmentValue = this.env
    ): DepValue {
        return evaluator.evaluate(
            EvaluationContext(toEvaluate, env, evaluator)
        )
    }

    fun <NEW_TYPE : Node> evaluateAsIdentifier(
        env: DepEnvironmentValue = this.env,
        block: TYPE.() -> NEW_TYPE
    ): DepIdentifier {
        return evaluateAsIdentifier(block(toEvaluate), env)
    }

    fun <TYPE : Node> evaluateAsIdentifier(
        toEvaluate: TYPE,
        env: DepEnvironmentValue = this.env
    ): DepIdentifier {
        val result = evaluate(toEvaluate, env)
        require(result is DepIdentifier)
        return result
    }

    fun <NEW_TYPE : Node> evaluateAsPrototype(
        env: DepEnvironmentValue = this.env,
        block: TYPE.() -> NEW_TYPE
    ): DepPrototypeValue {
        return evaluateAsPrototype(block(toEvaluate), env)
    }

    fun <TYPE : Node> evaluateAsPrototype(
        toEvaluate: TYPE,
        env: DepEnvironmentValue = this.env
    ): DepPrototypeValue {
        val result = evaluate(toEvaluate, env)
        require(result is DepPrototypeValue)
        return result
    }

    fun <NEW_TYPE : Node> evaluateAsString(
        env: DepEnvironmentValue = this.env,
        block: TYPE.() -> NEW_TYPE
    ): DepStringValue {
        return evaluateAsString(block(toEvaluate), env)
    }

    fun <TYPE : Node> evaluateAsString(
        toEvaluate: TYPE,
        env: DepEnvironmentValue = this.env
    ): DepStringValue {
        val result = evaluate(toEvaluate, env)
        require(result is DepStringValue)
        return result
    }

    fun <SELF : DepValue, OTHER : DepValue> evaluateInfix(
        operator: Operator,
        self: SELF,
        other: OTHER
    ): DepValue {
        val operationType = resolveInfixOperationType(operator)

        val selfValue = if (self is DepIdentifier) {
            env[self]
        } else {
            self
        }

        val otherValue = if (other is DepIdentifier) {
            env[other]
        } else {
            other
        }

        val operation = requireNotNull(selfValue.findInfixOperation(operationType, otherValue.type())) {
            "No infix operation specified for: ${selfValue.type()} $operator ${otherValue.type()}"
        }

        return operation(selfValue, otherValue)
    }

    fun <SELF : DepValue> evaluatePrefix(
        operator: Operator,
        self: SELF,
    ): DepValue {
        val operationType = resolvePrefixOperationType(operator)
        val operation = requireNotNull(self.findPrefixOperation(operationType)) {
            "No prefix operation specified for: $operationType ${self.type()}"
        }
        return operation(self)
    }
}

private fun resolveInfixOperationType(operator: Operator): DepValueOperation.Type {
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

private fun resolvePrefixOperationType(operator: Operator): DepValueOperation.Type {
    return when {
        operator == Operator.Minus -> Negate
        else -> TODO()
    }
}