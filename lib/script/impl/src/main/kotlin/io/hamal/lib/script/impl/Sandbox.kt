package io.hamal.lib.script.impl

import io.hamal.lib.script.api.Sandbox
import io.hamal.lib.script.api.value.EnvValue
import io.hamal.lib.script.api.value.FuncInvocationContext
import io.hamal.lib.script.api.value.FuncInvocationContextFactory
import io.hamal.lib.script.api.value.Value
import io.hamal.lib.script.impl.ast.parse
import io.hamal.lib.script.impl.token.tokenize

class DefaultSandbox<INVOKE_CTX : FuncInvocationContext>(
    private val env: EnvValue,
    funcInvocationContextFactory: FuncInvocationContextFactory<INVOKE_CTX>
) : Sandbox {

    private val interpreter = DefaultInterpreter(funcInvocationContextFactory)

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
