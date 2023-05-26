package io.hamal.lib.web3.util

import io.hamal.lib.web3.eth.abi.EthHexString
import io.hamal.lib.web3.eth.abi.EthPrefixedHexString
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.*
import org.junit.jupiter.api.*
import java.nio.ByteBuffer

class ByteWindowTest {

    @Nested
    
    inner class ConstructorTest {

        @Test
        fun ok() {
            val buffer = ByteBuffer.wrap(
                Web3Parser.parseHex(
                    "00000000000000000000000000000000000000000000000000000000000000047465737400000000000000000000000000000000000000000000000000000000"
                )
            )

            val result = ByteWindow(buffer, 32)
            assertThat(result.windowSize, equalTo(32))
            assertThat(result.remaining(), equalTo(64))
        }

        @Test
        fun `Input is not multiple of window size`() {
            val buffer = ByteBuffer.wrap(
                Web3Parser.parseHex(
                    "00000000000000000000000000000000000000000000000000000000000000047465737400000000000000000000000000000000000000000000000000000000"
                )
            )

            val exception = assertThrows<IllegalArgumentException> {
                ByteWindow(buffer, 10)
            }

            assertThat(exception.message, equalTo("Input size is not multiple of window size 10"))
        }


        @Test
        fun `Empty Input`() {
            val buffer = ByteBuffer.wrap(ByteArray(0))
            val exception = assertThrows<IllegalArgumentException> {
                ByteWindow(buffer, 16)
            }
            assertThat(exception.message, equalTo("Input size must >= window size 16"))
        }

    }

    @Nested
    
    inner class OfTest {

        @Test
        fun `Of byte array`() {
            val array = EthPrefixedHexString(
                "0x00000000000000000000000000000000000000000000000000000000000000047465737400000000000000000000000000000000000000000000000000000000"
            ).toByteArray()


            val result = ByteWindow.of(array)
            assertThat(result.remaining(), equalTo(64))
            assertThat(result.windowSize, equalTo(32))
        }

        @Test
        fun `Of prefixed hex string`() {
            val result = ByteWindow.of(
                EthPrefixedHexString(
                    "0x00000000000000000000000000000000000000000000000000000000000000047465737400000000000000000000000000000000000000000000000000000000"
                )
            )

            assertThat(result.remaining(), equalTo(64))
            assertThat(result.windowSize, equalTo(32))
        }

        @Test
        fun `Of empty prefixed hex string`() {
            val result = ByteWindow.of(EthPrefixedHexString("0x"))
            assertThat(result.remaining(), equalTo(32))
            assertThat(result.windowSize, equalTo(32))
        }

        @Test
        fun `Of hex string`() {
            val result = ByteWindow.of(
                EthHexString(
                    "00000000000000000000000000000000000000000000000000000000000000047465737400000000000000000000000000000000000000000000000000000000"
                )
            )

            assertThat(result.remaining(), equalTo(64))
            assertThat(result.windowSize, equalTo(32))
        }

        @Test
        fun `Of empty hex string`() {
            val result = ByteWindow.of(EthHexString(""))

            assertThat(result.remaining(), equalTo(32))
            assertThat(result.windowSize, equalTo(32))
        }

        @Test
        fun `Max Uint256`() {
            val result =
                ByteWindow.of(EthPrefixedHexString("0xffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"))
            assertThat(result.remaining(), equalTo(32))
            assertThat(result.windowSize, equalTo(32))
        }
    }

    @Nested
    
    inner class NexTest {

        @Test
        fun `Consumes all data`() {
            val result = testInstance.next(64)
            assertThat(result.size, equalTo(64))
            assertThat(testInstance.remaining(), equalTo(0))
            assertThat(testInstance.windowSize, equalTo(16))
        }

        @Test
        fun `Multiple invocations with window size`() {
            var result = testInstance.next()
            assertThat(result.size, equalTo(16))
            assertThat(testInstance.remaining(), equalTo(48))

            result = testInstance.next()
            assertThat(result.size, equalTo(16))
            assertThat(testInstance.remaining(), equalTo(32))

            result = testInstance.next()
            assertThat(result.size, equalTo(16))
            assertThat(testInstance.remaining(), equalTo(16))

            result = testInstance.next()
            assertThat(result.size, equalTo(16))
            assertThat(testInstance.remaining(), equalTo(0))
        }

        @Test
        fun `Multiple invocations with custom window size`() {
            var result = testInstance.next(16)
            assertThat(result.size, equalTo(16))
            assertThat(testInstance.remaining(), equalTo(48))

            result = testInstance.next(16)
            assertThat(result.size, equalTo(16))
            assertThat(testInstance.remaining(), equalTo(32))

            result = testInstance.next(32)
            assertThat(result.size, equalTo(32))
            assertThat(testInstance.remaining(), equalTo(0))
        }

        @Test
        fun `Subsequent invocation reads more than remaining`() {
            val result = testInstance.next(64)
            assertThat(result.size, equalTo(64))
            assertThat(testInstance.remaining(), equalTo(0))

            val exception = assertThrows<IllegalArgumentException> { testInstance.next(64) }
            assertThat(exception.message, equalTo("Tries to consume 64 bytes but only 0 bytes are available"))

        }

        @Test
        fun `Tries to consume more data than available`() {
            val exception = assertThrows<IllegalArgumentException> {
                testInstance.next(128)
            }
            assertThat(exception.message, equalTo("Tries to consume 128 bytes but only 64 bytes are available"))
        }

        @Test
        fun `Tries to read not multiple of window size`() {
            val exception = assertThrows<IllegalArgumentException> {
                testInstance.next(40)
            }
            assertThat(exception.message, equalTo("Tries to consume 40 bytes which is not multiple of window size 16"))
        }

        @Test
        fun `Tries to read nothing`() {
            val exception = assertThrows<IllegalArgumentException> {
                testInstance.next(0)
            }
            assertThat(exception.message, equalTo("Tries to consume 0 bytes"))
        }

        private val testInstance = ByteWindow.of(
            EthPrefixedHexString(
                """0x00000000000000000000000000000000000000000000000000000000000000047465737400000000000000000000000000000000000000000000000000000000"""
            ),
            16
        )
    }
}