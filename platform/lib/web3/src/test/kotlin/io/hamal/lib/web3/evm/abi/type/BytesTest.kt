package io.hamal.lib.web3.evm.abi.type

import io.hamal.lib.web3.evm.abi.type.EvmBytes32
import io.hamal.lib.web3.evm.abi.type.EvmPrefixedHexString
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test


internal class EvmBytes32Test {
    @Test
    fun `0x`() {
        val expected = EvmBytes32(EvmPrefixedHexString("0x0000000000000000000000000000000000000000000000000000000000000000"))

        EvmBytes32(EvmPrefixedHexString("0x")).also { result ->
            assertThat(result.value.size, equalTo(32))
            assertThat(result, equalTo(expected))
        }
    }

    @Test
    fun `0x0654380000000000000000000000000000000000000000000000000000000000`() {
        EvmBytes32(EvmPrefixedHexString("0x0654380000000000000000000000000000000000000000000000000000000000")).also { result ->
            assertThat(result.value.size, equalTo(32))
            assertThat(result.toPrefixedHexString(), equalTo(EvmPrefixedHexString("0x0654380000000000000000000000000000000000000000000000000000000000")))
        }
    }

    @Test
    fun `0x546974616e2028746974616e6275696c6465722e78797a29`() {
        EvmBytes32(EvmPrefixedHexString("0x546974616e2028746974616e6275696c6465722e78797a29")).also { result ->
            assertThat(result.value.size, equalTo(32))
            assertThat(result.toPrefixedHexString(), equalTo(EvmPrefixedHexString("0x0000000000000000546974616e2028746974616e6275696c6465722e78797a29")))
        }
    }
}