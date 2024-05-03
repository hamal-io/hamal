package io.hamal.lib.web3.evm

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import io.hamal.lib.common.serialization.AdapterJson
import io.hamal.lib.common.serialization.SerdeModuleJson
import io.hamal.lib.web3.evm.abi.type.*
import io.hamal.lib.web3.evm.domain.EvmMethod
import io.hamal.lib.web3.evm.domain.EvmRequestId
import java.lang.reflect.Type

object SerdeModuleJsonEvm : SerdeModuleJson() {

    init {
        set(EvmRequestId::class, EvmRequestId.Adapter)
        set(EvmMethod::class, EvmMethod.Adapter)
        set(EvmUint8::class, object : AdapterJson<EvmUint8> {
            override fun serialize(p0: EvmUint8?, p1: Type?, p2: JsonSerializationContext?): JsonElement {
                return JsonPrimitive(p0!!.toPrefixedHexString().toString())
            }

            override fun deserialize(p0: JsonElement?, p1: Type?, p2: JsonDeserializationContext?): EvmUint8 {
                return EvmUint8(EvmPrefixedHexString(p0!!.asString))
            }

        })
        set(EvmUint32::class, object : AdapterJson<EvmUint32> {
            override fun serialize(p0: EvmUint32?, p1: Type?, p2: JsonSerializationContext?): JsonElement {
                return JsonPrimitive(p0!!.toPrefixedHexString().toString())
            }

            override fun deserialize(p0: JsonElement?, p1: Type?, p2: JsonDeserializationContext?): EvmUint32 {
                return EvmUint32(EvmPrefixedHexString(p0!!.asString))
            }

        })
        set(EvmUint64::class, object : AdapterJson<EvmUint64> {
            override fun serialize(p0: EvmUint64?, p1: Type?, p2: JsonSerializationContext?): JsonElement {
                return JsonPrimitive(p0!!.toPrefixedHexString().toString())
            }

            override fun deserialize(p0: JsonElement?, p1: Type?, p2: JsonDeserializationContext?): EvmUint64 {
                return EvmUint64(EvmPrefixedHexString(p0!!.asString))
            }

        })
        set(EvmUint256::class, object : AdapterJson<EvmUint256> {
            override fun serialize(p0: EvmUint256?, p1: Type?, p2: JsonSerializationContext?): JsonElement {
                return JsonPrimitive(p0!!.toPrefixedHexString().toString())
            }

            override fun deserialize(p0: JsonElement?, p1: Type?, p2: JsonDeserializationContext?): EvmUint256 {
                return EvmUint256(EvmPrefixedHexString(p0!!.asString))
            }

        })
        set(EvmHash::class, object : AdapterJson<EvmHash> {
            override fun serialize(p0: EvmHash?, p1: Type?, p2: JsonSerializationContext?): JsonElement {
                return JsonPrimitive(p0!!.toPrefixedHexString().toString())
            }

            override fun deserialize(p0: JsonElement?, p1: Type?, p2: JsonDeserializationContext?): EvmHash {
                return EvmHash(EvmPrefixedHexString(p0!!.asString))
            }

        })
        set(EvmPrefixedHexString::class, object : AdapterJson<EvmPrefixedHexString> {
            override fun serialize(p0: EvmPrefixedHexString?, p1: Type?, p2: JsonSerializationContext?): JsonElement {
                return JsonPrimitive(p0!!.toString())
            }

            override fun deserialize(
                p0: JsonElement?,
                p1: Type?,
                p2: JsonDeserializationContext?
            ): EvmPrefixedHexString {
                return EvmPrefixedHexString(EvmPrefixedHexString(p0!!.asString))
            }

        })
        set(EvmBytes32::class, object : AdapterJson<EvmBytes32> {
            override fun serialize(p0: EvmBytes32?, p1: Type?, p2: JsonSerializationContext?): JsonElement {
                return JsonPrimitive(p0!!.toPrefixedHexString().toString())
            }

            override fun deserialize(p0: JsonElement?, p1: Type?, p2: JsonDeserializationContext?): EvmBytes32 {
                return EvmBytes32((p0!!.asString))
            }

        })
        set(EvmAddress::class, object : AdapterJson<EvmAddress> {
            override fun serialize(p0: EvmAddress?, p1: Type?, p2: JsonSerializationContext?): JsonElement {
                return JsonPrimitive(p0!!.toPrefixedHexString().toString())
            }

            override fun deserialize(p0: JsonElement?, p1: Type?, p2: JsonDeserializationContext?): EvmAddress {
                return EvmAddress(EvmPrefixedHexString(p0!!.asString))
            }

        })
    }
}
