package io.hamal.lib.web3.eth.abi

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.api.DynamicTest.*
import java.math.BigInteger


@Nested
class AddressTest {

    @Test
    fun `Zero constant`() {
        val result = EthAddress.Zero
        assertThat(result.value, equalTo(EthUint160(BigInteger.ZERO)))
        assertThat(
            result.toPrefixedHexString(),
            equalTo(EthPrefixedHexString("0x0000000000000000000000000000000000000000"))
        )
    }

    @Test
    fun `Null constant`() {
        val result = EthAddress.Null
        assertThat(result.value, equalTo(EthUint160(BigInteger("1158896792795502070752211396329834747757200325310"))))
        assertThat(
            result.toPrefixedHexString(),
            equalTo(EthPrefixedHexString("0x0000000000000000000000000000000000000000"))
        )
    }

    @Nested
    inner class ConstructorTest {
        @Test
        @DisplayName("ok")
        fun ok() {
            val testInstance = EthAddress(BigInteger.valueOf(42))
            assertThat(testInstance.value, equalTo(EthUint160(BigInteger.valueOf(42))))
        }
    }

    @Nested
    inner class OfTest {
        @Test
        fun `Address of hex string`() {
            val testInstance = EthAddress.of(
                EthHexString("0000000000000000000000000000000000000002")
            )
            assertThat(testInstance.value, equalTo(EthUint160(BigInteger.valueOf(2))))
        }

        @Test
        fun `Address of prefixed hex string`() {
            val testInstance = EthAddress.of(
                EthPrefixedHexString("0x0000000000000000000000000000000000000002")
            )
            assertThat(testInstance.value, equalTo(EthUint160(BigInteger.valueOf(2))))
        }

        @Test
        fun `Address is case sensitive`() {
            val testInstance = EthAddress.of(
                EthPrefixedHexString("0x161A48a5c229bF3e66BEcA46A50B01d36F201CD4")
            )
            assertThat(testInstance.toString(), equalTo("0x161A48a5c229bF3e66BEcA46A50B01d36F201CD4"))
        }

        @Test
        fun `Address with all input is lower case`() {
            val testInstance = EthAddress.of(
                EthPrefixedHexString("0x161a48a5c229bf3e66beca46a50b01d36f201cd4")
            )
            assertThat(testInstance.toString(), equalTo("0x161A48a5c229bF3e66BEcA46A50B01d36F201CD4"))
        }

        @Test
        fun `Address with all input is upper case`() {
            val testInstance = EthAddress.of(
                EthPrefixedHexString("0x161A48A5C229BF3E66BECA46A50B01D36F201CD4")
            )
            assertThat(testInstance.toString(), equalTo("0x161A48a5c229bF3e66BEcA46A50B01d36F201CD4"))
        }

        @Test
        fun `Address does not match regex pattern`() {
            val exception = assertThrows<IllegalArgumentException> {
                EthAddress.of(
                    EthPrefixedHexString("0x161A48a5c229bF3e66BEcA46A50B01d36F201CD")
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
                EthAddress.of(
                    EthPrefixedHexString("0x161A48a5c229bF3e66BEcA46A50B01d36F201CD")
                )
            }
            assertThat(
                exception.message,
                equalTo("161A48a5c229bF3e66BEcA46A50B01d36F201CD does not match address pattern")
            )
        }
    }

}

@DisplayName("ValidateAddress()")
class EnsureValidAddressTest {

    @TestFactory
    fun `Valid addresses`() = listOf(
        EthHexString("161A48a5c229bF3e66BEcA46A50B01d36F201CD4"),
        EthHexString("0000000000000000000000000000000000000001"),
        EthHexString("161a48a5c229bf3e66beca46a50b01d36f201cd4"),
        EthHexString("161A48A5C229BF3E66BECA46A50B01D36F201CD4"),
    ).map { validAddress ->
        dynamicTest("$validAddress") {
            ValidateAddress(validAddress)
        }
    }

    @TestFactory
    fun `Invalid addresses`() = listOf(
        EthHexString("161A48a5c229bF3e66BEcA46A50B01d36F201CD"),
        EthHexString("161A48a5c229bF3e66BEcA46A50B01d36F201CD41"),
        EthHexString("00be5422d15f39373eb0a97ff8c10fbd0e40e29338"),
        EthHexString("161A48A5c229bF3e66BEcA46A50B01d36F201CD4"),
        EthHexString("161a48a5C229Bf3E66beCa46a50b01D36f201cd4"),
    ).map { validAddress ->
        dynamicTest("$validAddress") {
            assertThrows<IllegalArgumentException> {
                ValidateAddress(validAddress)
            }
        }
    }
}