package io.hamal.lib.web3.eth.abi

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

object EthInputTuple0Test {
    @Test
    fun `plainSignature`() {
        val result = testInstance.plainSignature()
        assertThat(result, equalTo("()"))
    }

    private val testInstance = EthInputTuple0
}

