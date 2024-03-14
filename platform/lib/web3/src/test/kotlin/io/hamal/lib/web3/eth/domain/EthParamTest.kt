package io.hamal.lib.web3.eth.domain

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal object EthBlockParamTest {
    @Test
    fun `Latest block`() {
        val result = EthBlockParameter.Latest
        assertThat(result, equalTo(EthBlockParameter("latest")))
    }

    @Test
    fun `Earliest block`() {
        val result = EthBlockParameter.Earliest
        assertThat(result, equalTo(EthBlockParameter("earliest")))
    }

    @Test
    fun `Pending block`() {
        val result = EthBlockParameter.Pending
        assertThat(result, equalTo(EthBlockParameter("pending")))
    }

    @Test
    fun `With block number`() {
        val result = EthBlockParameter(42L)
        assertThat(result, equalTo(EthBlockParameter("0x2a")))
    }

    @Test
    fun `Tries to set negative block number`() {
        val exception = assertThrows<IllegalArgumentException> {
            EthBlockParameter(-1)
        }
        assertThat(exception.message, equalTo("Block number must not be negative"))
    }

}