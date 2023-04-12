package io.hamal.module.worker.script.ast.expr

import io.hamal.lib.meta.Tuple2
import io.hamal.module.worker.script.ast.Operator
import io.hamal.module.worker.script.ast.Parser
import io.hamal.module.worker.script.token.Token
import io.hamal.module.worker.script.token.Token.Type.Plus
import org.hamcrest.MatcherAssert.*
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class InfixTest {

    @Nested
    @DisplayName("parseOperator()")
    @TestInstance(Lifecycle.PER_CLASS)
    inner class ParseOperatorTest {

        private fun tokens(vararg types: Token.Type) =
            types.mapIndexed { index, type -> Token(type, 1, index, type.value) }

        private fun testCases(): List<Tuple2<List<Token>, Operator>> {
            return listOf(
                Tuple2(tokens(Plus), Operator.Plus),
            );
        }

        @ParameterizedTest(name = "#{index} - Test tokens {0}")
        @MethodSource("testCases")
        fun `Parameterized tests`(arg: Tuple2<List<Token>, Operator>) {
            val result = Parser.Context(ArrayDeque(arg._1)).parseOperator()
            val expected = arg._2;
            assertThat(result, equalTo(expected));
        }
    }
}