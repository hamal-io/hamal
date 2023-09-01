package io.hamal.lib.web3.eth.abi.type

import io.hamal.lib.web3.util.ByteWindow
import kotlin.reflect.KClass

sealed interface EthType<VALUE : Any> {
    val value: VALUE
    fun toByteArray(): ByteArray
    fun toByteWindow(): ByteWindow

    companion object {
        fun <T : EthType<*>> solidityType(clazz: KClass<T>): String = solidityMapping[clazz]!!

        private val solidityMapping = mapOf(
            EthAddress::class to "address",
            EthBool::class to "bool",
            EthBytes32::class to "bytes32",
            EthHash::class to "hash",
            EthString::class to "string",
            EthUint8::class to "uint8",
            EthUint16::class to "uint16",
            EthUint32::class to "uint32",
            EthUint64::class to "uint64",
            EthUint112::class to "uint112",
            EthUint128::class to "uint128",
            EthUint160::class to "uint160",
            EthUint256::class to "uint256",
        )
    }
}



