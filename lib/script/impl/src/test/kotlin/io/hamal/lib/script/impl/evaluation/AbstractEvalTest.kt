package io.hamal.lib.script.impl.evaluation

import io.hamal.lib.script.api.Environment
import io.hamal.lib.script.api.value.Value
import io.hamal.lib.script.impl.ast.parse
import io.hamal.lib.script.impl.interpreter.Interpreter
import io.hamal.lib.script.impl.interpreter.RootEnvironment
import io.hamal.lib.script.impl.token.tokenize

internal abstract class AbstractEvalTest {

    protected val testEnvironment: Environment = RootEnvironment()
    protected val testInterpreter = Interpreter.DefaultImpl

    fun eval(code: String): Value {
        val tokens = tokenize(code)
        val statements = parse(tokens)
        return testInterpreter.run(statements, testEnvironment)
    }

}