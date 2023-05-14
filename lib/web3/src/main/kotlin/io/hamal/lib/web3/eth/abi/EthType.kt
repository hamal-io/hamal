package io.hamal.lib.web3.eth.abi

import io.hamal.lib.web3.util.ByteWindow

sealed interface EthType<VALUE : Any> {
    val value: VALUE

//    val type: Type
//    enum class Type(
//        val solidityType: kotlin.String
//    ) {
//        Address("address"),
//        Bool("bool"),
//        Bytes16("bytes16"),
//        Bytes32("bytes32"),
//        Hash("hash"),
//        String("string"),
//        Uint8("uint8"),
//        Uint16("uint16"),
//        Uint32("uint32"),
//        Uint64("uint64"),
//        Uint112("uint112"),
//        Uint128("uint128"),
//        Uint160("uint160"),
//        Uint256("uint256")
//    }

    fun toByteArray(): ByteArray

    fun toByteWindow(): ByteWindow
}


