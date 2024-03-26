package io.hamal.core.adapter.auth

import io.hamal.lib.domain.request.AuthLogInMetaMaskRequest
import io.hamal.lib.domain.vo.Web3Address
import io.hamal.lib.domain.vo.Web3Signature
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows


class VerifySignatureTest {

    @Test
    fun `Address of signature matches given address`() {
        verifySignature(object : AuthLogInMetaMaskRequest {
            override val address = Web3Address("0xB72D984fb7E4763B57526CEf25A749EeC5334B93")
            override val signature = Web3Signature(
                "0x511005f84ca22940f94c281d2888987d824377fd661f11fc1deae9dbcde4ac4d6c77fdd4d4f2c75dddf56148218937adbd4c0ebd31cb5f834641d31b5e775a001b"
            )
        })
    }

    @TestFactory
    fun `Address of signature matches is case insensitive`() = listOf(
        "0xB72D984fb7E4763B57526CEf25A749EeC5334B93",
        "0xb72d984fb7e4763b57526cef25a749eec5334b93",
        "0XB72D984FB7E4763B57526CEF25A749EEC5334B93"
    ).map { addressString ->
        dynamicTest(addressString) {
            verifySignature(object : AuthLogInMetaMaskRequest {
                override val address = Web3Address(addressString)
                override val signature = Web3Signature(
                    "0x511005f84ca22940f94c281d2888987d824377fd661f11fc1deae9dbcde4ac4d6c77fdd4d4f2c75dddf56148218937adbd4c0ebd31cb5f834641d31b5e775a001b"
                )
            })
        }
    }


    @Test
    fun `Address of signature does not match given address`() {
        assertThrows<NoSuchElementException> {
            verifySignature(object : AuthLogInMetaMaskRequest {
                override val address = Web3Address("0x0000000000000000000000000000000000000000")
                override val signature = Web3Signature(
                    "0x511005f84ca22940f94c281d2888987d824377fd661f11fc1deae9dbcde4ac4d6c77fdd4d4f2c75dddf56148218937adbd4c0ebd31cb5f834641d31b5e775a001b"
                )
            })
        }.also { e -> assertThat(e.message, equalTo("Account not found")) }
    }
}