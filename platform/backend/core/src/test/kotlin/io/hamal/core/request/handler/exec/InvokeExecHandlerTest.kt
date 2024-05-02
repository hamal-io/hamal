package io.hamal.core.request.handler.exec

import io.hamal.lib.common.value.ValueObject
import io.hamal.lib.domain.vo.ExecInputs
import io.hamal.lib.domain.vo.FuncInputs
import io.hamal.lib.domain.vo.InvocationInputs
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
            ValueObject.builder()
                .set("key", 2810)
                .set("invoke", "invoke")
                .build()
        )
        val result = invocationInputs.toExecInputs()
        assertThat(
            result, equalTo(
                ExecInputs(
                    ValueObject.builder()
                        .set("key", 2810)
                        .set("invoke", "invoke")
                        .build()
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
            ValueObject.builder()
                .set("key", 1)
                .set("func", "func")
                .build()
        )
        val invocationInputs = InvocationInputs(
            ValueObject.builder()
                .set("key", 2810)
                .set("invoke", "invoke")
                .build()
        )
        val result = merge(funcInputs, invocationInputs)
        assertThat(
            result, equalTo(
                ExecInputs(
                    ValueObject.builder()
                        .set("key", 2810)
                        .set("func", "func")
                        .set("invoke", "invoke")
                        .build()
                )
            )
        )
    }
}