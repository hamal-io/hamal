package io.hamal.lib.web3.util

import io.hamal.lib.web3.evm.abi.type.*
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test


class FormatToHexTest {
    @Test
    fun `Formats a byte array into a hexadecimal string without checksum`() {
        val someBytes = EvmHexString("161a48a5c229bf3e66beca46a50b01d36f201cd4").toByteArray()
        val result = Web3Formatter.formatToHex(someBytes)
        assertThat(result, equalTo("161a48a5c229bf3e66beca46a50b01d36f201cd4"))
    }

    @Test
    fun `Formats an empty array`() {
        val result = Web3Formatter.formatToHex(ByteArray(0))
        assertThat(result, equalTo(""))
    }
}


class FormatWithoutLeadingZerosTest {

    @Test
    fun `ethuint64 - 0xcdeac4a94`() {
        val bytes = EvmUint64(EvmPrefixedHexString("0xcdeac4a94")).toByteArray()
        val result = Web3Formatter.formatWithoutLeadingZeros(bytes)
        assertThat(result, equalTo("cdeac4a94"))
    }

    @Test
    fun `With leading zero in byte array`() {
        val bytes = EvmBytes32(EvmPrefixedHexString("0x546974616e2028746974616e6275696c6465722e78797a29")).toByteArray()
        val result = Web3Formatter.formatWithoutLeadingZeros(bytes)
        assertThat(result, equalTo("546974616e2028746974616e6275696c6465722e78797a29"))
    }

    @Test
    fun `With many leading zeros`() {
        val bytes = EvmAddress.of(EvmPrefixedHexString("0x0000000000000000000000000000000000000001"))
            .toByteWindow()
            .next()

        val result = Web3Formatter.formatWithoutLeadingZeros(bytes)
        assertThat(result, equalTo("1"))
    }

    @Test
    fun `With no leading zero`() {
        val bytes = EvmAddress.of(EvmPrefixedHexString("0x1000000000000000000000000000000000000000"))
            .toByteWindow()
            .next()

        val result = Web3Formatter.formatWithoutLeadingZeros(bytes)
        assertThat(result, equalTo("1000000000000000000000000000000000000000"))
    }
}

class FormatFixLengthTest {
    @Test
    fun `Tries to format empty array`() {
        val result = Web3Formatter.formatFixLength(ByteArray(0), 10)
        assertThat(result, equalTo("0000000000"))
    }

    @Test
    fun `Result hex string fits exactly limit`() {
        val someBytes = EvmHexString("161a48a5c229bf3e66beca46a50b01d36f201cd4").toByteArray()
        val result = Web3Formatter.formatFixLength(someBytes, 40)
        assertThat(result, equalTo("161a48a5c229bf3e66beca46a50b01d36f201cd4"))
    }

    @Test
    fun `Result hex string is less than limit`() {
        val someBytes = EvmHexString("acab").toByteArray()
        val result = Web3Formatter.formatFixLength(someBytes, 6)
        assertThat(result, equalTo("00acab"))
    }

    @Test
    fun `Result hex string longer than limit`() {
        val someBytes = EvmHexString("0987acab").toByteArray()
        val result = Web3Formatter.formatFixLength(someBytes, 4)
        assertThat(result, equalTo("acab"))
    }
}
