package io.hamal.script

import io.hamal.script.ast.parse
import io.hamal.script.eval.Environment
import io.hamal.script.eval.Eval
import io.hamal.script.token.tokenize
import io.hamal.script.value.Value

class Sandbox {

    private val env: Environment = Environment()
    private val eval = Eval.DefaultImpl()


//    init {
//        env.assignLocal("assert", pbke)
//    }

    fun eval(code: String): Value {
        val tokens = tokenize(code)
        val statements = parse(tokens)
        try {
            return eval(statements, env)
        } catch (e: ScriptEvaluationException) {
            return e.error
        } catch (t: Throwable) {
            throw t
        }
    }

}
