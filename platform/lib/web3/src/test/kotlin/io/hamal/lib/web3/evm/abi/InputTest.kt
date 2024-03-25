package io.hamal.lib.web3.evm.abi

import io.hamal.lib.web3.evm.abi.EthInputTuple0
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

object EvmInputTuple0Test {
    @Test
    fun `signature`() {
        val result = testInstance.signature
        assertThat(result, equalTo("()"))
    }

    private val testInstance = EthInputTuple0
}

