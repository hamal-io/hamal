package io.hamal.lib.web3.eth.domain

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows

class ErrorCodeTest {
    @TestFactory
    fun `fromCode`() = EthError.ErrorCode.values
        .map { errorCode ->
            dynamicTest("$errorCode") {
                val result = EthError.ErrorCode.fromCode(errorCode.value)
                assertThat(result, equalTo(errorCode))
            }
        }

    @Test
    fun `fromCode() - Not found`() {
        val exception = assertThrows<NoSuchElementException> {
            EthError.ErrorCode.fromCode(-23)
        }
        assertThat(exception.message, equalTo("Error code not found"))
    }
}