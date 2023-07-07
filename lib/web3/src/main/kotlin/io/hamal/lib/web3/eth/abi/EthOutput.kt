package io.hamal.lib.web3.eth.abi

import io.hamal.lib.domain.Tuple1
import io.hamal.lib.domain.Tuple2
import io.hamal.lib.domain.Tuple3
import io.hamal.lib.web3.eth.abi.type.*
import io.hamal.lib.web3.util.ByteWindow
import java.util.concurrent.atomic.AtomicReference
import java.util.function.Consumer
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
    }
}


interface OutputTuple {
    fun decodeToMap(data: EthPrefixedHexString): Map<String, EthType<*>> {
        val result = mutableMapOf<String, EthType<*>>()
        decode(data) { decoded ->
            decoded.forEach {
                result[it._1] = it._2
            }
        }
        return result
    }

    fun decode(data: EthPrefixedHexString, decoded: Consumer<Sequence<Tuple2<String, EthType<*>>>>)
    fun concatenatedTypes(): String
}


internal class OutputTuple0 : OutputTuple {
    override fun decodeToMap(data: EthPrefixedHexString): Map<String, EthType<*>> {
        return mapOf()
    }

    override fun decode(data: EthPrefixedHexString, decoded: Consumer<Sequence<Tuple2<String, EthType<*>>>>) {
        decoded.accept(sequenceOf())
    }

    override fun concatenatedTypes(): String {
        return ""
    }
}


internal data class OutputTuple1<VALUE_1 : EthType<*>, ARG_1 : EthOutput<VALUE_1>>(
    val _1: ARG_1
) : OutputTuple {
    fun decode(data: EthPrefixedHexString): Tuple1<VALUE_1> {
        val result: AtomicReference<Tuple1<VALUE_1>> = AtomicReference()
        decode(data) { decoded ->
            val decodedList: List<Tuple2<String, EthType<*>>> = decoded.toList()
            @Suppress("UNCHECKED_CAST")
            result.set(Tuple1(decodedList[0]._2 as VALUE_1))
        }
        return result.get()
    }

    override fun decode(data: EthPrefixedHexString, decoded: Consumer<Sequence<Tuple2<String, EthType<*>>>>) {
        val window = data.toByteWindow()
        decoded.accept(
            sequenceOf(
                Tuple2(_1.name, _1.decode(window))
            )
        )
    }

    override fun concatenatedTypes(): String {
//        return _1.type.solidityType()
        TODO()
    }
}


internal data class OutputTuple2<
        VALUE_1 : EthType<*>, ARG_1 : EthOutput<VALUE_1>,
        VALUE_2 : EthType<*>, ARG_2 : EthOutput<VALUE_2>
        >(
    val _1: ARG_1,
    val _2: ARG_2
) : OutputTuple {
    fun decode(data: EthPrefixedHexString): Tuple2<VALUE_1, VALUE_2> {
        val result = AtomicReference<Tuple2<VALUE_1, VALUE_2>>()
        decode(data) { decoded ->
            val decodedList = decoded.toList()
            @Suppress("UNCHECKED_CAST")
            result.set(
                Tuple2(
                    decodedList[0]._2 as VALUE_1,
                    decodedList[1]._2 as VALUE_2
                )
            )
        }
        return result.get()
    }

    override fun decode(
        data: EthPrefixedHexString,
        decoded: Consumer<Sequence<Tuple2<String, EthType<*>>>>
    ) {
        val window = data.toByteWindow()
        decoded.accept(
            sequenceOf(
                Tuple2(_1.name, _1.decode(window)),
                Tuple2(_2.name, _2.decode(window))
            )
        )
    }

    override fun concatenatedTypes(): String {
//        return _1.type.solidityType() + ',' + _2.type.solidityType()
        TODO()
    }
}


internal data class OutputTuple3<
        VALUE_1 : EthType<*>, ARG_1 : EthOutput<VALUE_1>,
        VALUE_2 : EthType<*>, ARG_2 : EthOutput<VALUE_2>,
        VALUE_3 : EthType<*>, ARG_3 : EthOutput<VALUE_2>
        >(
    val _1: ARG_1,
    val _2: ARG_2,
    val _3: ARG_3,
) : OutputTuple {
    fun decode(data: EthPrefixedHexString): Tuple3<VALUE_1, VALUE_2, VALUE_3> {
        val result = AtomicReference<Tuple3<VALUE_1, VALUE_2, VALUE_3>>()
        decode(data) { decoded ->
            val decodedList = decoded.toList()
            @Suppress("UNCHECKED_CAST")
            result.set(
                Tuple3(
                    decodedList[0]._2 as VALUE_1,
                    decodedList[1]._2 as VALUE_2,
                    decodedList[2]._2 as VALUE_3
                )
            )
        }
        return result.get()
    }

    override fun decode(
        data: EthPrefixedHexString,
        decoded: Consumer<Sequence<Tuple2<String, EthType<*>>>>
    ) {
        val window = data.toByteWindow()
        decoded.accept(
            sequenceOf(
                Tuple2(_1.name, _1.decode(window)),
                Tuple2(_2.name, _2.decode(window)),
                Tuple2(_3.name, _3.decode(window))
            )
        )
    }

    override fun concatenatedTypes(): String {
//        return _1.type.solidityType() + ',' + _2.type.solidityType() + ',' + _3.type.solidityType()
        TODO()
    }
}
