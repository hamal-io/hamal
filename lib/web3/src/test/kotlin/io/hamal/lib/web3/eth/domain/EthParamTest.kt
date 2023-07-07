package io.hamal.lib.web3.eth.domain

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal object EthBlockParamTest {
    @Test
    fun `Latest block`() {
        val result = EthBlockParam.Latest
        assertThat(result, equalTo(EthBlockParam("latest")))
    }

    @Test
    fun `Earliest block`() {
        val result = EthBlockParam.Earliest
        assertThat(result, equalTo(EthBlockParam("earliest")))
    }

    @Test
    fun `Pending block`() {
        val result = EthBlockParam.Pending
        assertThat(result, equalTo(EthBlockParam("pending")))
    }

    @Test
    fun `With block number`() {
        val result = EthBlockParam(42L)
        assertThat(result, equalTo(EthBlockParam("0x2a")))
    }

    @Test
    fun `Tries to set negative block number`() {
        val exception = assertThrows<IllegalArgumentException> {
            EthBlockParam(-1)
        }
        assertThat(exception.message, equalTo("Block number must not be negative"))
    }

}