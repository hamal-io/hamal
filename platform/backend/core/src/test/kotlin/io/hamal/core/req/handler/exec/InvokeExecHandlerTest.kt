package io.hamal.core.request.handler.exec

import io.hamal.lib.domain.vo.ExecInputs
import io.hamal.lib.domain.vo.FuncInputs
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.kua.type.KuaMap
import io.hamal.lib.kua.type.KuaNumber
import io.hamal.lib.kua.type.KuaString
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
            KuaMap(
                mutableMapOf(
                    "key" to KuaNumber(2810),
                    "invoke" to KuaString("invoke")
                )
            )
        )
        val result = invocationInputs.toExecInputs()
        assertThat(
            result, equalTo(
                ExecInputs(
                    KuaMap(
                        mutableMapOf(
                            "key" to KuaNumber(2810),
                            "invoke" to KuaString("invoke")
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
            KuaMap(
                mutableMapOf(
                    "key" to KuaNumber(1),
                    "func" to KuaString("func")
                )
            )
        )
        val invocationInputs = InvocationInputs(
            KuaMap(
                mutableMapOf(
                    "key" to KuaNumber(2810),
                    "invoke" to KuaString("invoke")
                )
            )
        )
        val result = merge(funcInputs, invocationInputs)
        assertThat(
            result, equalTo(
                ExecInputs(
                    KuaMap(
                        mutableMapOf(
                            "key" to KuaNumber(2810),
                            "func" to KuaString("func"),
                            "invoke" to KuaString("invoke")
                        )
                    )
                )
            )
        )
    }
}