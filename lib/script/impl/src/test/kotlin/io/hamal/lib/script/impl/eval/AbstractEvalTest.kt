package io.hamal.lib.script.impl.eval

import io.hamal.lib.script.api.value.EnvironmentValue
import io.hamal.lib.script.api.value.Identifier
import io.hamal.lib.script.api.value.Value
import io.hamal.lib.script.impl.DefaultInterpreter
import io.hamal.lib.script.impl.ast.parse
import io.hamal.lib.script.impl.builtin.AssertFunction
import io.hamal.lib.script.impl.builtin.RequireFunction
import io.hamal.lib.script.impl.token.tokenize

internal abstract class AbstractEvalTest {

    protected val testEnvironment: EnvironmentValue = EnvironmentValue(
        Identifier("_G"),
        values = mapOf(
            AssertFunction.identifier to AssertFunction,
            RequireFunction.identifier to RequireFunction
        )
    )
    protected val testInterpreter = DefaultInterpreter

    fun eval(code: String): Value {
        val tokens = tokenize(code)
        val statements = parse(tokens)
        return testInterpreter.run(statements, testEnvironment)
    }

}