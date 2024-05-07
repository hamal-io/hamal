package io.hamal.lib.web3.evm.abi.type

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import io.hamal.lib.common.serialization.AdapterJson
import io.hamal.lib.web3.util.ByteWindow
import java.lang.reflect.Type
import kotlin.reflect.KClass

sealed interface EvmType<VALUE : Any> {
    val value: VALUE
    fun toByteArray(): ByteArray
    fun toByteWindow(): ByteWindow

    companion object {
        fun <T : EvmType<*>> solidityType(clazz: KClass<T>): String = solidityMapping[clazz]!!

        private val solidityMapping = mapOf(
            EvmAddress::class to "address",
            EvmBool::class to "bool",
            EvmBytes32::class to "bytes32",
            EvmHash::class to "hash",
            EvmString::class to "string",
            EvmUint8::class to "uint8",
            EvmUint16::class to "uint16",
            EvmUint32::class to "uint32",
            EvmUint64::class to "uint64",
            EvmUint112::class to "uint112",
            EvmUint128::class to "uint128",
            EvmUint160::class to "uint160",
            EvmUint256::class to "uint256",
        )
    }

    class Adapter<TYPE : EvmType<*>>(
        val ctor: (EvmPrefixedHexString) -> TYPE
    ) : AdapterJson<TYPE> {

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): TYPE {
            return ctor(EvmPrefixedHexString(json.asString))
        }

        override fun serialize(src: TYPE, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return JsonPrimitive(EvmPrefixedHexString(src.toString()).value)
        }
    }
}



