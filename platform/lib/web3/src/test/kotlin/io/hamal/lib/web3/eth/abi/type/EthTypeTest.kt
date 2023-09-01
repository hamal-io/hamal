package io.hamal.lib.web3.eth.abi.type

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory

class SolidityTypeTest {
    @TestFactory
    fun `Solidity Type`() = listOf(
        EthAddress::class,
        EthBool::class,
        EthBytes32::class,
        EthHash::class,
        EthString::class,
        EthUint8::class,
        EthUint16::class,
        EthUint32::class,
        EthUint64::class,
        EthUint112::class,
        EthUint128::class,
        EthUint160::class,
        EthUint256::class,
    ).map { clazz ->
        dynamicTest("${clazz.simpleName}") {
            val expected = clazz.simpleName!!.lowercase().substring(3)
            assertThat(EthType.solidityType(clazz), equalTo(expected))
        }
    }
}