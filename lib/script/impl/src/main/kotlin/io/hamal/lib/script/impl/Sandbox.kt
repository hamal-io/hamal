package io.hamal.lib.script.impl

import io.hamal.lib.script.api.Sandbox
import io.hamal.lib.script.api.value.EnvValue
import io.hamal.lib.script.api.value.Value
import io.hamal.lib.script.impl.ast.parse
import io.hamal.lib.script.impl.token.tokenize

class DefaultSandbox(
    private val env: EnvValue
) : Sandbox {

    private val interpreter = DefaultInterpreter()

    override fun eval(code: String): Value {
        val tokens = tokenize(code)
        val statements = parse(tokens)
        return try {
            interpreter.run(statements, env)
        } catch (e: ExitException) {
            e.status
        } catch (e: ScriptEvaluationException) {
            e.error
        } catch (t: Throwable) {
            throw t
        }
    }

}
