package io.hamal.script

import io.hamal.script.ast.parse
import io.hamal.script.eval.Environment
import io.hamal.script.eval.Eval
import io.hamal.script.token.tokenize
import io.hamal.script.value.Value

class Sandbox {

    private val env: Environment = Environment()
    private val eval = Eval.DefaultImpl()

    fun eval(code: String): Value {
        val tokens = tokenize(code)
        val statements = parse(tokens)
        return eval(statements, env)
    }

}
