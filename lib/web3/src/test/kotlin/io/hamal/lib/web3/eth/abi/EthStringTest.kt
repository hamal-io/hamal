package io.hamal.lib.web3.eth.abi

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.DynamicTest.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows

class EthHexStringTest {
    @Nested
    
    inner class ConstructorTest {
        @TestFactory
        fun `Valid hex string`() = listOf(
            "",
            "0000000000000000000000000000000000000000",
        ).map { validHexString ->
            dynamicTest("$validHexString is valid hex string") {
                EthHexString(validHexString)
            }
        }

        @TestFactory
        fun `Invalid hex string`() = listOf(
            "0x1234",
        ).map { invalidHexString ->
            dynamicTest("$invalidHexString is invalid hex string") {
                assertThrows<IllegalArgumentException> {
                    EthHexString(invalidHexString)
                }
            }
        }
    }
}

class EthPrefixedHexStringTest {
    @Nested
    
    inner class ConstructorTest {
        @TestFactory
        fun `Valid hex string`() = listOf(
            "0x",
            "0x1234",
        ).map { validHexString ->
            dynamicTest("$validHexString is valid prefixed hex string") {
                EthPrefixedHexString(validHexString)
            }
        }

        @TestFactory
        fun `Invalid hex string`() = listOf(
            "",
            "0000000000000000000000000000000000000000"
        ).map { invalidHexString ->
            dynamicTest("$invalidHexString is invalid prefixed hex string") {
                assertThrows<IllegalArgumentException> {
                    EthPrefixedHexString(invalidHexString)
                }
            }
        }
    }
}


class ValidateHexStringTest {

    @TestFactory
    fun `Valid hex string`() = listOf(
        "",
        "0123456789ABCDEFabcdef",
        "161A48a5c229bF3e66BEcA46A50B01d36F201CD4",
        "0000000000000000000000000000000000000001",
        "0000000000000000000000000000000000000000",
    ).map { validHexString ->
        dynamicTest("$validHexString is valid hex string") {
            ValidateHexString(validHexString)
        }
    }

    @TestFactory
    fun `Invalid hex string`() = listOf(
        "         ",
        " 0000000000000000000000000000000000000000",
        "0000000000000000000000000000000000000000 ",
        "0x1234",
        "Invalid",
    ).map { invalidHexString ->
        dynamicTest("$invalidHexString is invalid hex string") {
            assertThrows<IllegalArgumentException> {
                ValidateHexString(invalidHexString)
            }
        }
    }

}