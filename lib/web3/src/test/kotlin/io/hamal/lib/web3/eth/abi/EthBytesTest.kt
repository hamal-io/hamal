package io.hamal.lib.web3.eth.abi

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@Nested
class EthBytesTest {
    @Nested
    @DisplayName("constructor()")
    inner class ConstructorTest {
        @Test
        fun `Byte array has exact size as specified`() {
            TestEthBytes(ByteArray(16), 16)
        }

        @Test
        fun `Byte array is smaller than specified`() {
            val exception = assertThrows<IllegalArgumentException> {
                TestEthBytes(ByteArray(10), 16)
            }
            assertThat(exception.message, equalTo("Requires array of 16 bytes"))
        }

        @Test
        fun `Byte array is larger than specified`() {
            val exception = assertThrows<IllegalArgumentException> {
                TestEthBytes(ByteArray(20), 16)
            }
            assertThat(exception.message, equalTo("Requires array of 16 bytes"))
        }

    }
}