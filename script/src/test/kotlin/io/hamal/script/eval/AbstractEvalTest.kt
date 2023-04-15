package io.hamal.script.eval

import io.hamal.script.ast.parse
import io.hamal.script.token.tokenize
import io.hamal.script.value.Value

internal abstract class AbstractEvalTest {

    protected val env: Environment = Environment()
    protected val eval = Eval.DefaultImpl()

    fun eval(code: String): Value {
        val tokens = tokenize(code)
        val statements = parse(tokens)
        return eval(statements, env)
    }

}