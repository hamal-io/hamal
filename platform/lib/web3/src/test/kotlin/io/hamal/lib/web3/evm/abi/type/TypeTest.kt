package io.hamal.lib.web3.evm.abi.type

import io.hamal.lib.web3.evm.abi.type.*
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory

class SolidityValueTypeTest {
    @TestFactory
    fun `Solidity Type`() = listOf(
        EvmAddress::class,
        EvmBool::class,
        EvmBytes32::class,
        EvmHash::class,
        EvmString::class,
        EvmUint8::class,
        EvmUint16::class,
        EvmUint32::class,
        EvmUint64::class,
        EvmUint112::class,
        EvmUint128::class,
        EvmUint160::class,
        EvmUint256::class,
    ).map { clazz ->
        dynamicTest("${clazz.simpleName}") {
            val expected = clazz.simpleName!!.lowercase().substring(3)
            assertThat(EvmType.solidityType(clazz), equalTo(expected))
        }
    }
}