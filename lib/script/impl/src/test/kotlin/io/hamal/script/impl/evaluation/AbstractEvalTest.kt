package io.hamal.script.impl.evaluation

import io.hamal.script.impl.ast.parse
import io.hamal.script.impl.interpreter.Environment
import io.hamal.script.impl.interpreter.Interpreter
import io.hamal.script.impl.token.tokenize
import io.hamal.script.api.value.Value

internal abstract class AbstractEvalTest {

    protected val env: Environment = Environment()
    protected val interpreter = Interpreter.DefaultImpl

    fun eval(code: String): Value {
        val tokens = tokenize(code)
        val statements = parse(tokens)
        return interpreter.run(statements, env)
    }

}