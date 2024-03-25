package io.hamal.lib.web3.evm.abi

import io.hamal.lib.web3.evm.abi.type.*
import io.hamal.lib.web3.evm.abi.type.EvmType.Companion.solidityType
import io.hamal.lib.web3.util.ByteWindow
import kotlin.reflect.KClass

data class EvmOutput<VALUE_TYPE : EvmType<*>>(
    val name: String,
    val clazz: KClass<VALUE_TYPE>,
    val decoder: EvmTypeDecoder<VALUE_TYPE>,
) {
    fun decode(window: ByteWindow) = decoder.decode(window)

    companion object {
        fun Address(name: String) = EvmOutput(name, EvmAddress::class, EvmTypeDecoder.Address)
        fun Byte32(name: String) = EvmOutput(name, EvmBytes32::class, EvmTypeDecoder.Byte32)
        fun Bool(name: String) = EvmOutput(name, EvmBool::class, EvmTypeDecoder.Bool)
        fun Uint8(name: String) = EvmOutput(name, EvmUint8::class, EvmTypeDecoder.Uint8)
        fun Uint16(name: String) = EvmOutput(name, EvmUint16::class, EvmTypeDecoder.Uint16)
        fun Uint32(name: String) = EvmOutput(name, EvmUint32::class, EvmTypeDecoder.Uint32)
        fun Uint64(name: String) = EvmOutput(name, EvmUint64::class, EvmTypeDecoder.Uint64)
        fun Uint112(name: String) = EvmOutput(name, EvmUint112::class, EvmTypeDecoder.Uint112)
        fun Uint128(name: String) = EvmOutput(name, EvmUint128::class, EvmTypeDecoder.Uint128)
        fun Uint160(name: String) = EvmOutput(name, EvmUint160::class, EvmTypeDecoder.Uint160)
        fun Uint256(name: String) = EvmOutput(name, EvmUint256::class, EvmTypeDecoder.Uint256)
        fun String(name: String) = EvmOutput(name, EvmString::class, EvmTypeDecoder.String)

        //@formatter:off
        fun Tuple0() = EvmOutputTuple0

        fun <
            VALUE_1 : EvmType<*>, ARG_1 : EvmOutput<VALUE_1>,
        > Tuple1(_1: ARG_1): EvmOutputTuple1<VALUE_1, ARG_1> {
            return EvmOutputTuple1(_1)
        }

        fun <
            VALUE_1 : EvmType<*>, ARG_1 : EvmOutput<VALUE_1>,
            VALUE_2 : EvmType<*>, ARG_2 : EvmOutput<VALUE_2>
        > Tuple2(_1: ARG_1, _2: ARG_2): EvmOutputTuple2<VALUE_1, ARG_1, VALUE_2, ARG_2> {
            return EvmOutputTuple2(_1, _2)
        }

        fun <
            VALUE_1 : EvmType<*>, ARG_1 : EvmOutput<VALUE_1>,
            VALUE_2 : EvmType<*>, ARG_2 : EvmOutput<VALUE_2>,
            VALUE_3 : EvmType<*>, ARG_3 : EvmOutput<VALUE_3>
        > Tuple3(_1: ARG_1, _2: ARG_2,_3: ARG_3): EvmOutputTuple3<VALUE_1, ARG_1, VALUE_2, ARG_2, VALUE_3, ARG_3> {
            return EvmOutputTuple3(_1, _2, _3)
        }

        //@formatter:on
    }
}


sealed interface EvmOutputTuple {
    fun decodeToMap(data: EvmPrefixedHexString): Map<String, EvmType<*>> {
        return decode(data).associateBy({ it.name }, { it.value })
    }

    fun decode(data: EvmPrefixedHexString): List<DecodedEvmType<*>>
    fun concatenatedTypes(): String
}


object EvmOutputTuple0 : EvmOutputTuple {
    override fun decodeToMap(data: EvmPrefixedHexString): Map<String, EvmType<*>> = mapOf()
    override fun decode(data: EvmPrefixedHexString): List<DecodedEvmType<*>> = listOf()
    override fun concatenatedTypes() = ""
}


data class EvmOutputTuple1<VALUE_1 : EvmType<*>, ARG_1 : EvmOutput<VALUE_1>>(
    val _1: ARG_1
) : EvmOutputTuple {
    override fun decode(data: EvmPrefixedHexString): List<DecodedEvmType<*>> {
        val window = data.toByteWindow()
        return listOf(DecodedEvmType(_1.name, _1.decode(window)))
    }

    override fun concatenatedTypes(): String {
        return solidityType(_1.clazz)
    }
}


data class EvmOutputTuple2<
        VALUE_1 : EvmType<*>, ARG_1 : EvmOutput<VALUE_1>,
        VALUE_2 : EvmType<*>, ARG_2 : EvmOutput<VALUE_2>
        >(
    val _1: ARG_1,
    val _2: ARG_2
) : EvmOutputTuple {

    override fun decode(data: EvmPrefixedHexString): List<DecodedEvmType<*>> {
        val window = data.toByteWindow()
        return listOf(
            DecodedEvmType(_1.name, _1.decode(window)),
            DecodedEvmType(_2.name, _2.decode(window))
        )
    }

    override fun concatenatedTypes() = "${solidityType(_1.clazz)},${solidityType(_2.clazz)}"
}


data class EvmOutputTuple3<
        VALUE_1 : EvmType<*>, ARG_1 : EvmOutput<VALUE_1>,
        VALUE_2 : EvmType<*>, ARG_2 : EvmOutput<VALUE_2>,
        VALUE_3 : EvmType<*>, ARG_3 : EvmOutput<VALUE_3>
        >(
    val _1: ARG_1,
    val _2: ARG_2,
    val _3: ARG_3,
) : EvmOutputTuple {
    override fun decode(data: EvmPrefixedHexString): List<DecodedEvmType<*>> {
        val window = data.toByteWindow()
        return listOf(
            DecodedEvmType(_1.name, _1.decode(window)),
            DecodedEvmType(_2.name, _2.decode(window)),
            DecodedEvmType(_3.name, _3.decode(window))
        )
    }

    override fun concatenatedTypes() = "${solidityType(_1.clazz)},${solidityType(_2.clazz)},${solidityType(_3.clazz)}"
}
