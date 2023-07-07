package io.hamal.lib.web3.eth.abi

import io.hamal.lib.web3.eth.abi.type.*
import io.hamal.lib.web3.eth.abi.type.EthType.Companion.solidityType
import io.hamal.lib.web3.util.ByteWindow
import kotlin.reflect.KClass

data class EthOutput<VALUE_TYPE : EthType<*>>(
    val name: String,
    val clazz: KClass<VALUE_TYPE>,
    val decoder: EthTypeDecoder<VALUE_TYPE>,
) {
    fun decode(window: ByteWindow) = decoder.decode(window)

    companion object {
        fun Address(name: String) = EthOutput(name, EthAddress::class, EthTypeDecoder.Address)
        fun Byte32(name: String) = EthOutput(name, EthBytes32::class, EthTypeDecoder.Byte32)
        fun Bool(name: String) = EthOutput(name, EthBool::class, EthTypeDecoder.Bool)
        fun Uint8(name: String) = EthOutput(name, EthUint8::class, EthTypeDecoder.Uint8)
        fun Uint16(name: String) = EthOutput(name, EthUint16::class, EthTypeDecoder.Uint16)
        fun Uint32(name: String) = EthOutput(name, EthUint32::class, EthTypeDecoder.Uint32)
        fun Uint64(name: String) = EthOutput(name, EthUint64::class, EthTypeDecoder.Uint64)
        fun Uint112(name: String) = EthOutput(name, EthUint112::class, EthTypeDecoder.Uint112)
        fun Uint128(name: String) = EthOutput(name, EthUint128::class, EthTypeDecoder.Uint128)
        fun Uint160(name: String) = EthOutput(name, EthUint160::class, EthTypeDecoder.Uint160)
        fun Uint256(name: String) = EthOutput(name, EthUint256::class, EthTypeDecoder.Uint256)
        fun String(name: String) = EthOutput(name, EthString::class, EthTypeDecoder.String)

        //@formatter:off
        fun Tuple0() = EthOutputTuple0

        fun <
            VALUE_1 : EthType<*>, ARG_1 : EthOutput<VALUE_1>,
        > Tuple1(_1: ARG_1): EthOutputTuple1<VALUE_1, ARG_1> {
            return EthOutputTuple1(_1)
        }

        fun <
            VALUE_1 : EthType<*>, ARG_1 : EthOutput<VALUE_1>,
            VALUE_2 : EthType<*>, ARG_2 : EthOutput<VALUE_2>
        > Tuple2(_1: ARG_1, _2: ARG_2): EthOutputTuple2<VALUE_1, ARG_1, VALUE_2, ARG_2> {
            return EthOutputTuple2(_1, _2)
        }

        fun <
            VALUE_1 : EthType<*>, ARG_1 : EthOutput<VALUE_1>,
            VALUE_2 : EthType<*>, ARG_2 : EthOutput<VALUE_2>,
            VALUE_3 : EthType<*>, ARG_3 : EthOutput<VALUE_3>
        > Tuple3(_1: ARG_1, _2: ARG_2,_3: ARG_3): EthOutputTuple3<VALUE_1, ARG_1, VALUE_2, ARG_2, VALUE_3, ARG_3> {
            return EthOutputTuple3(_1, _2, _3)
        }

        //@formatter:on
    }
}


sealed interface EthOutputTuple {
    fun decodeToMap(data: EthPrefixedHexString): Map<String, EthType<*>> {
        return decode(data).associateBy({ it.name }, { it.value })
    }

    fun decode(data: EthPrefixedHexString): List<DecodedEthType<*>>
    fun concatenatedTypes(): String
}


object EthOutputTuple0 : EthOutputTuple {
    override fun decodeToMap(data: EthPrefixedHexString): Map<String, EthType<*>> = mapOf()
    override fun decode(data: EthPrefixedHexString): List<DecodedEthType<*>> = listOf()
    override fun concatenatedTypes() = ""
}


data class EthOutputTuple1<VALUE_1 : EthType<*>, ARG_1 : EthOutput<VALUE_1>>(
    val _1: ARG_1
) : EthOutputTuple {
    override fun decode(data: EthPrefixedHexString): List<DecodedEthType<*>> {
        val window = data.toByteWindow()
        return listOf(DecodedEthType(_1.name, _1.decode(window)))
    }

    override fun concatenatedTypes(): String {
        return solidityType(_1.clazz)
    }
}


data class EthOutputTuple2<
        VALUE_1 : EthType<*>, ARG_1 : EthOutput<VALUE_1>,
        VALUE_2 : EthType<*>, ARG_2 : EthOutput<VALUE_2>
        >(
    val _1: ARG_1,
    val _2: ARG_2
) : EthOutputTuple {

    override fun decode(data: EthPrefixedHexString): List<DecodedEthType<*>> {
        val window = data.toByteWindow()
        return listOf(
            DecodedEthType(_1.name, _1.decode(window)),
            DecodedEthType(_2.name, _2.decode(window))
        )
    }

    override fun concatenatedTypes() = "${solidityType(_1.clazz)},${solidityType(_2.clazz)}"
}


data class EthOutputTuple3<
        VALUE_1 : EthType<*>, ARG_1 : EthOutput<VALUE_1>,
        VALUE_2 : EthType<*>, ARG_2 : EthOutput<VALUE_2>,
        VALUE_3 : EthType<*>, ARG_3 : EthOutput<VALUE_3>
        >(
    val _1: ARG_1,
    val _2: ARG_2,
    val _3: ARG_3,
) : EthOutputTuple {
    override fun decode(data: EthPrefixedHexString): List<DecodedEthType<*>> {
        val window = data.toByteWindow()
        return listOf(
            DecodedEthType(_1.name, _1.decode(window)),
            DecodedEthType(_2.name, _2.decode(window)),
            DecodedEthType(_3.name, _3.decode(window))
        )
    }

    override fun concatenatedTypes() = "${solidityType(_1.clazz)},${solidityType(_2.clazz)},${solidityType(_3.clazz)}"
}
