package io.hamal.backend.req.handler.exec

import io.hamal.lib.domain.vo.ExecInputs
import io.hamal.lib.domain.vo.FuncInputs
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.NumberType
import io.hamal.lib.kua.type.StringType
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class ToExecInputsTest {
    @Test
    fun `Empty invocation inputs`() {
        val invocationInputs = InvocationInputs()
        val result = invocationInputs.toExecInputs()
        assertThat(result, equalTo(ExecInputs()))
    }

    @Test
    fun ok() {
        val invocationInputs = InvocationInputs(
            MapType(
                mutableMapOf(
                    "key" to NumberType(2810),
                    "invoke" to StringType("invoke")
                )
            )
        )
        val result = invocationInputs.toExecInputs()
        assertThat(
            result, equalTo(
                ExecInputs(
                    MapType(
                        mutableMapOf(
                            "key" to NumberType(2810),
                            "invoke" to StringType("invoke")
                        )
                    )
                )
            )
        )
    }
}

class MergeTest {
    @Test
    fun `func inputs and invocation inputs are both empty`() {
        val funcInputs = FuncInputs()
        val invocationInputs = InvocationInputs()
        val result = merge(funcInputs, invocationInputs)
        assertThat(result, equalTo(ExecInputs()))
    }

    @Test
    fun `invocation inputs overrides func inputs`() {
        val funcInputs = FuncInputs(
            MapType(
                mutableMapOf(
                    "key" to NumberType(1),
                    "func" to StringType("func")
                )
            )
        )
        val invocationInputs = InvocationInputs(
            MapType(
                mutableMapOf(
                    "key" to NumberType(2810),
                    "invoke" to StringType("invoke")
                )
            )
        )
        val result = merge(funcInputs, invocationInputs)
        assertThat(
            result, equalTo(
                ExecInputs(
                    MapType(
                        mutableMapOf(
                            "key" to NumberType(2810),
                            "func" to StringType("func"),
                            "invoke" to StringType("invoke")
                        )
                    )
                )
            )
        )
    }
}