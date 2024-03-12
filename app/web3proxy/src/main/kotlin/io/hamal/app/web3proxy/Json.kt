package io.hamal.app.web3proxy

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import io.hamal.lib.common.hot.HotJsonModule
import io.hamal.lib.common.serialization.JsonAdapter
import io.hamal.lib.common.serialization.JsonFactoryBuilder
import io.hamal.lib.common.serialization.JsonModule
import io.hamal.lib.domain.Json
import io.hamal.lib.domain.vo.ValueObjectJsonModule
import io.hamal.lib.web3.eth.abi.type.*
import io.hamal.lib.web3.eth.domain.EthGetBlockByNumberRequest
import io.hamal.lib.web3.eth.domain.EthMethod
import io.hamal.lib.web3.eth.domain.EthRequest
import io.hamal.lib.web3.eth.domain.EthRequestId
import java.lang.reflect.Type

object EthModule : JsonModule() {
    init {
        set(EthRequestId::class, EthRequestId.Adapter)
        set(EthMethod::class, EthMethod.Adapter)
        set(EthRequest::class, EthRequest.Adapter)
        set(EthGetBlockByNumberRequest::class, EthGetBlockByNumberRequest.Adapter)
        set(EthUint8::class, object : JsonAdapter<EthUint8> {
            override fun serialize(p0: EthUint8?, p1: Type?, p2: JsonSerializationContext?): JsonElement {
                return JsonPrimitive(p0!!.toPrefixedHexString().toString())
            }

            override fun deserialize(p0: JsonElement?, p1: Type?, p2: JsonDeserializationContext?): EthUint8 {
                return EthUint8(EthPrefixedHexString(p0!!.asString))
            }

        })
        set(EthUint32::class, object : JsonAdapter<EthUint32> {
            override fun serialize(p0: EthUint32?, p1: Type?, p2: JsonSerializationContext?): JsonElement {
                return JsonPrimitive(p0!!.toPrefixedHexString().toString())
            }

            override fun deserialize(p0: JsonElement?, p1: Type?, p2: JsonDeserializationContext?): EthUint32 {
                return EthUint32(EthPrefixedHexString(p0!!.asString))
            }

        })
        set(EthUint64::class, object : JsonAdapter<EthUint64> {
            override fun serialize(p0: EthUint64?, p1: Type?, p2: JsonSerializationContext?): JsonElement {
                return JsonPrimitive(p0!!.toPrefixedHexString().toString())
            }

            override fun deserialize(p0: JsonElement?, p1: Type?, p2: JsonDeserializationContext?): EthUint64 {
                return EthUint64(EthPrefixedHexString(p0!!.asString))
            }

        })
        set(EthUint256::class, object : JsonAdapter<EthUint256> {
            override fun serialize(p0: EthUint256?, p1: Type?, p2: JsonSerializationContext?): JsonElement {
                return JsonPrimitive(p0!!.toPrefixedHexString().toString())
            }

            override fun deserialize(p0: JsonElement?, p1: Type?, p2: JsonDeserializationContext?): EthUint256 {
                return EthUint256(EthPrefixedHexString(p0!!.asString))
            }

        })
        set(EthHash::class, object : JsonAdapter<EthHash> {
            override fun serialize(p0: EthHash?, p1: Type?, p2: JsonSerializationContext?): JsonElement {
                return JsonPrimitive(p0!!.toPrefixedHexString().toString())
            }

            override fun deserialize(p0: JsonElement?, p1: Type?, p2: JsonDeserializationContext?): EthHash {
                return EthHash(EthPrefixedHexString(p0!!.asString))
            }

        })
        set(EthPrefixedHexString::class, object : JsonAdapter<EthPrefixedHexString> {
            override fun serialize(p0: EthPrefixedHexString?, p1: Type?, p2: JsonSerializationContext?): JsonElement {
                return JsonPrimitive(p0!!.toString())
            }

            override fun deserialize(
                p0: JsonElement?,
                p1: Type?,
                p2: JsonDeserializationContext?
            ): EthPrefixedHexString {
                return EthPrefixedHexString(EthPrefixedHexString(p0!!.asString))
            }

        })
        set(EthBytes32::class, object : JsonAdapter<EthBytes32> {
            override fun serialize(p0: EthBytes32?, p1: Type?, p2: JsonSerializationContext?): JsonElement {
                return JsonPrimitive(p0!!.toPrefixedHexString().toString())
            }

            override fun deserialize(p0: JsonElement?, p1: Type?, p2: JsonDeserializationContext?): EthBytes32 {
                return EthBytes32(EthPrefixedHexString(p0!!.asString))
            }

        })
        set(EthAddress::class, object : JsonAdapter<EthAddress> {
            override fun serialize(p0: EthAddress?, p1: Type?, p2: JsonSerializationContext?): JsonElement {
                return JsonPrimitive(p0!!.toPrefixedHexString().toString())
            }

            override fun deserialize(p0: JsonElement?, p1: Type?, p2: JsonDeserializationContext?): EthAddress {
                return EthAddress(EthPrefixedHexString(p0!!.asString))
            }

        })
    }
}

val json = Json(
    JsonFactoryBuilder()
        .register(HotJsonModule)
        .register(ValueObjectJsonModule)
        .register(EthModule)

)