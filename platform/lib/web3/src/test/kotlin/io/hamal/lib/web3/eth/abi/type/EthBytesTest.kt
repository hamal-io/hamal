package io.hamal.lib.web3.eth.abi.type

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test


internal class EthBytes32Test {
    @Test
    fun `0x`() {
        val expected = EthBytes32(EthPrefixedHexString("0x0000000000000000000000000000000000000000000000000000000000000000"))

        EthBytes32(EthPrefixedHexString("0x")).also { result ->
            assertThat(result.value.size, equalTo(32))
            assertThat(result, equalTo(expected))
        }
    }

    @Test
    fun `0x0654380000000000000000000000000000000000000000000000000000000000`() {
        EthBytes32(EthPrefixedHexString("0x0654380000000000000000000000000000000000000000000000000000000000")).also { result ->
            assertThat(result.value.size, equalTo(32))
            assertThat(result.toPrefixedHexString(), equalTo(EthPrefixedHexString("0x0654380000000000000000000000000000000000000000000000000000000000")))
        }
    }

    @Test
    fun `0x546974616e2028746974616e6275696c6465722e78797a29`() {
        EthBytes32(EthPrefixedHexString("0x546974616e2028746974616e6275696c6465722e78797a29")).also { result ->
            assertThat(result.value.size, equalTo(32))
            assertThat(result.toPrefixedHexString(), equalTo(EthPrefixedHexString("0x0000000000000000546974616e2028746974616e6275696c6465722e78797a29")))
        }
    }
}