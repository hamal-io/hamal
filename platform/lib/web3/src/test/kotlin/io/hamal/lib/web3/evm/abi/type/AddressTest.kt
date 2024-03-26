package io.hamal.lib.web3.evm.abi.type

import io.hamal.lib.web3.evm.abi.type.*
import io.hamal.lib.web3.evm.abi.type.ValidateAddress
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows
import java.math.BigInteger


@Nested
internal class AddressTest {

    @Test
    fun `Zero constant`() {
        val result = EvmAddress.Zero
        assertThat(result.value, equalTo(EvmUint160(BigInteger.ZERO)))
        assertThat(
            result.toPrefixedHexString(),
            equalTo(EvmPrefixedHexString("0x0000000000000000000000000000000000000000"))
        )
    }

    @Test
    fun `Null constant`() {
        val result = EvmAddress.Null
        assertThat(result.value, equalTo(EvmUint160(BigInteger("1158896792795502070752211396329834747757200325310"))))
        assertThat(
            result.toPrefixedHexString(),
            equalTo(EvmPrefixedHexString("0x0000000000000000000000000000000000000000"))
        )
    }

    @Nested
    inner class ConstructorTest {
        @Test

        fun ok() {
            val testInstance = EvmAddress(BigInteger.valueOf(42))
            assertThat(testInstance.value, equalTo(EvmUint160(BigInteger.valueOf(42))))
        }
    }

    @Nested
    inner class OfTest {
        @Test
        fun `Address of hex string`() {
            val testInstance = EvmAddress.of(
                EvmHexString("0000000000000000000000000000000000000002")
            )
            assertThat(testInstance.value, equalTo(EvmUint160(BigInteger.valueOf(2))))
        }

        @Test
        fun `Address of prefixed hex string`() {
            val testInstance = EvmAddress.of(
                EvmPrefixedHexString("0x0000000000000000000000000000000000000002")
            )
            assertThat(testInstance.value, equalTo(EvmUint160(BigInteger.valueOf(2))))
        }

        @Test
        fun `Address is case sensitive`() {
            val testInstance = EvmAddress.of(
                EvmPrefixedHexString("0x161A48a5c229bF3e66BEcA46A50B01d36F201CD4")
            )
            assertThat(testInstance.toString(), equalTo("0x161A48a5c229bF3e66BEcA46A50B01d36F201CD4"))
        }

        @Test
        fun `Address with all input is lower case`() {
            val testInstance = EvmAddress.of(
                EvmPrefixedHexString("0x161a48a5c229bf3e66beca46a50b01d36f201cd4")
            )
            assertThat(testInstance.toString(), equalTo("0x161A48a5c229bF3e66BEcA46A50B01d36F201CD4"))
        }

        @Test
        fun `Address with all input is upper case`() {
            val testInstance = EvmAddress.of(
                EvmPrefixedHexString("0x161A48A5C229BF3E66BECA46A50B01D36F201CD4")
            )
            assertThat(testInstance.toString(), equalTo("0x161A48a5c229bF3e66BEcA46A50B01d36F201CD4"))
        }

        @Test
        fun `Address does not match regex pattern`() {
            val exception = assertThrows<IllegalArgumentException> {
                EvmAddress.of(
                    EvmPrefixedHexString("0x161A48a5c229bF3e66BEcA46A50B01d36F201CD")
                )
            }
            assertThat(
                exception.message,
                equalTo("161A48a5c229bF3e66BEcA46A50B01d36F201CD does not match address pattern")
            )
        }

        @Test
        fun `Address does not match encoding`() {
            val exception = assertThrows<IllegalArgumentException> {
                EvmAddress.of(
                    EvmPrefixedHexString("0x161A48a5c229bF3e66BEcA46A50B01d36F201CD")
                )
            }
            assertThat(
                exception.message,
                equalTo("161A48a5c229bF3e66BEcA46A50B01d36F201CD does not match address pattern")
            )
        }
    }

}


class EnsureValidAddressTest {

    @TestFactory
    fun `Valid addresses`() = listOf(
        EvmHexString("161A48a5c229bF3e66BEcA46A50B01d36F201CD4"),
        EvmHexString("0000000000000000000000000000000000000001"),
        EvmHexString("161a48a5c229bf3e66beca46a50b01d36f201cd4"),
        EvmHexString("161A48A5C229BF3E66BECA46A50B01D36F201CD4"),
    ).map { validAddress ->
        dynamicTest("$validAddress") {
            ValidateAddress(validAddress)
        }
    }

    @TestFactory
    fun `Invalid addresses`() = listOf(
        EvmHexString("161A48a5c229bF3e66BEcA46A50B01d36F201CD"),
        EvmHexString("161A48a5c229bF3e66BEcA46A50B01d36F201CD41"),
        EvmHexString("00be5422d15f39373eb0a97ff8c10fbd0e40e29338"),
        EvmHexString("161A48A5c229bF3e66BEcA46A50B01d36F201CD4"),
        EvmHexString("161a48a5C229Bf3E66beCa46a50b01D36f201cd4"),
    ).map { validAddress ->
        dynamicTest("$validAddress") {
            assertThrows<IllegalArgumentException> {
                ValidateAddress(validAddress)
            }
        }
    }
}