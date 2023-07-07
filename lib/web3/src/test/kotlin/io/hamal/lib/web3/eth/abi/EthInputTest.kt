package io.hamal.lib.web3.eth.abi

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

object EthInputTuple0Test {
    @Test
    fun `signature`() {
        val result = testInstance.signature
        assertThat(result, equalTo("()"))
    }

    private val testInstance = EthInputTuple0
}

