package io.hamal.backend.instance.req.handler.exec

import io.hamal.lib.domain.vo.ExecInputs
import io.hamal.lib.domain.vo.FuncInputs
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.kua.type.DoubleType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.kua.type.TableType
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
            TableType(
                StringType("key") to DoubleType(2810),
                StringType("invoke") to StringType("invoke")
            )
        )
        val result = invocationInputs.toExecInputs()
        assertThat(
            result, equalTo(
                ExecInputs(
                    TableType(
                        StringType("key") to DoubleType(2810),
                        StringType("invoke") to StringType("invoke")
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
            TableType(
                StringType("key") to DoubleType(1),
                StringType("func") to StringType("func")
            )
        )
        val invocationInputs = InvocationInputs(
            TableType(
                StringType("key") to DoubleType(2810),
                StringType("invoke") to StringType("invoke")
            )
        )
        val result = merge(funcInputs, invocationInputs)
        assertThat(
            result, equalTo(
                ExecInputs(
                    TableType(
                        StringType("key") to DoubleType(2810),
                        StringType("func") to StringType("func"),
                        StringType("invoke") to StringType("invoke")
                    )
                )
            )
        )
    }
}