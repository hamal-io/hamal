package io.hamal.lib.web3.evm.domain

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal object EvmBlockParameterTest {

    @Test
    fun `Latest block`() {
        val result = EvmBlockParameter.Latest
        assertThat(result, equalTo(EvmBlockParameter("latest")))
    }

    @Test
    fun `Earliest block`() {
        val result = EvmBlockParameter.Earliest
        assertThat(result, equalTo(EvmBlockParameter("earliest")))
    }

    @Test
    fun `Pending block`() {
        val result = EvmBlockParameter.Pending
        assertThat(result, equalTo(EvmBlockParameter("pending")))
    }

    @Test
    fun `With block number`() {
        val result = EvmBlockParameter(42L)
        assertThat(result, equalTo(EvmBlockParameter("0x2a")))
    }

    @Test
    fun `Tries to set negative block number`() {
        val exception = assertThrows<IllegalArgumentException> {
            EvmBlockParameter(-1)
        }
        assertThat(exception.message, equalTo("Block number must not be negative"))
    }

}