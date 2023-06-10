package io.hamal.lib.script.impl.eval

import io.hamal.lib.script.api.value.DefaultFuncInvocationContextFactory
import io.hamal.lib.script.api.value.EnvValue
import io.hamal.lib.script.api.value.IdentValue
import io.hamal.lib.script.api.value.Value
import io.hamal.lib.script.impl.DefaultInterpreter
import io.hamal.lib.script.impl.ast.parse
import io.hamal.lib.script.impl.builtin.AssertFunction
import io.hamal.lib.script.impl.builtin.RequireFunction
import io.hamal.lib.script.impl.token.tokenize
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest

internal typealias TestFunction = (result: Value, env: EnvValue) -> Unit

fun equalToValue(expected: Value): (result: Value, _: EnvValue) -> Unit {
    return { result, _ -> assertThat(result, CoreMatchers.equalTo(expected)) }
}

internal abstract class AbstractEvalTest {

    private fun eval(code: String, testFn: TestFunction) {
        val tokens = tokenize(code)
        val statements = parse(tokens)

        val testInterpreter = DefaultInterpreter(DefaultFuncInvocationContextFactory)

        val testEnv = EnvValue(
            IdentValue("_G"),
            values = mapOf(
                IdentValue("assert") to AssertFunction,
                IdentValue("require") to RequireFunction
            )
        )

        val result = testInterpreter.run(statements, testEnv)
        testFn(result, testEnv)
    }

    protected fun prepareTests(tests: List<Pair<String, TestFunction>>): List<DynamicTest> = tests
        .map { (code, testFn) -> dynamicTest(code) { eval(code, testFn) } }
}