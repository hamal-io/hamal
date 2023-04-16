package io.hamal.script.evaluation

import io.hamal.script.ast.parse
import io.hamal.script.interpreter.Environment
import io.hamal.script.interpreter.Interpreter
import io.hamal.script.token.tokenize
import io.hamal.script.value.Value

internal abstract class AbstractEvalTest {

    protected val env: Environment = Environment()
    protected val interpreter = Interpreter.DefaultImpl

    fun eval(code: String): Value {
        val tokens = tokenize(code)
        val statements = parse(tokens)
        return interpreter.run(statements, env)
    }

}