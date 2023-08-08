package io.hamal.backend.instance.req.handler.exec

import io.hamal.lib.domain.vo.ExecInputs
import io.hamal.lib.domain.vo.FuncInputs
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.kua.value.NumberValue
import io.hamal.lib.kua.value.StringValue
import io.hamal.lib.kua.value.TableValue
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
            TableValue(
                StringValue("key") to NumberValue(2810),
                StringValue("invoke") to StringValue("invoke")
            )
        )
        val result = invocationInputs.toExecInputs()
        assertThat(
            result, equalTo(
                ExecInputs(
                    TableValue(
                        StringValue("key") to NumberValue(2810),
                        StringValue("invoke") to StringValue("invoke")
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
            TableValue(
                StringValue("key") to NumberValue(1),
                StringValue("func") to StringValue("func")
            )
        )
        val invocationInputs = InvocationInputs(
            TableValue(
                StringValue("key") to NumberValue(2810),
                StringValue("invoke") to StringValue("invoke")
            )
        )
        val result = merge(funcInputs, invocationInputs)
        assertThat(
            result, equalTo(
                ExecInputs(
                    TableValue(
                        StringValue("key") to NumberValue(2810),
                        StringValue("func") to StringValue("func"),
                        StringValue("invoke") to StringValue("invoke")
                    )
                )
            )
        )
    }
}