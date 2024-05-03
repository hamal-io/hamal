package io.hamal.lib.web3.evm.domain

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import io.hamal.lib.common.serialization.AdapterJson
import java.lang.reflect.Type

data class EvmRequestId(val value: String) {
    object Adapter : AdapterJson<EvmRequestId> {

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): EvmRequestId {
            return EvmRequestId(json.asString)
        }

        override fun serialize(src: EvmRequestId, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return JsonPrimitive(src.value)
        }
    }
}

interface EvmRequest{
    val id: EvmRequestId
    val method: EvmMethod
}