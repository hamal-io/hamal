package io.hamal.lib.web3.eth.domain

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import io.hamal.lib.common.serialization.JsonAdapter
import java.lang.reflect.Type

data class EthRequestId(val value: Int) {
    object Adapter : JsonAdapter<EthRequestId> {

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): EthRequestId {
            return EthRequestId(json.asInt)
        }

        override fun serialize(src: EthRequestId, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return JsonPrimitive(src.value)
        }
    }
}