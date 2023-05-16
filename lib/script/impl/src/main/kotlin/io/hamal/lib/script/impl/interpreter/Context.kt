package io.hamal.lib.script.impl.interpreter

import io.hamal.lib.script.api.Environment
import io.hamal.lib.script.api.ast.Node
import io.hamal.lib.script.api.value.Identifier
import io.hamal.lib.script.api.value.StringValue
import io.hamal.lib.script.api.value.Value
import io.hamal.lib.script.api.value.ValueOperation
import io.hamal.lib.script.api.value.ValueOperation.Type.Add
import io.hamal.lib.script.api.value.ValueOperation.Type.Sub
import io.hamal.lib.script.impl.ast.expr.Operator
import io.hamal.lib.script.impl.value.PrototypeValue

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

    fun <SELF : Value, OTHER : Value> evaluateInfix(
        operator: Operator,
        self: SELF,
        other: OTHER
    ): Value {
        val operationType = resolveOperationType(operator)
        val operation = self.findInfixOperation(operationType, other.type())!!
        return operation(self, other)
    }
}

private fun resolveOperationType(operator: Operator): ValueOperation.Type {
    return when {
        operator == Operator.Plus -> Add
        operator == Operator.Minus -> Sub
        else -> TODO()
    }
}