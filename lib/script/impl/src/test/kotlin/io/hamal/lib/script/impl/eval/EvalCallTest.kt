package io.hamal.lib.script.impl.eval

import io.hamal.lib.kua.value.NumberValue
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.TestFactory

internal class EvalCallTest : AbstractEvalTest() {

    @TestFactory
    fun evalCallTests() = prepareTests(
        listOf(
            """
            function answer() return 42  end
            answer()
        """ to { result, _ -> assertThat(result, equalTo(NumberValue(42))) },

            """
            function answer() return 42  end
            answer()
            answer()
            answer()
        """ to { result, _ -> assertThat(result, equalTo(NumberValue(42))) },
        )
    )
}