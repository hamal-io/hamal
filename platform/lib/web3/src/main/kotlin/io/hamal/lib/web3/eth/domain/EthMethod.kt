package io.hamal.lib.web3.eth.domain

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import io.hamal.lib.common.serialization.JsonAdapter
import java.lang.reflect.Type

enum class EthMethod(val value: String) {
    Call("eth_call"),
    GetBlockByNumber("eth_getBlockByNumber");

    companion object {
        fun of(value: String): EthMethod {
            return entries.find { it.value == value } ?: throw NoSuchElementException("EthMethod not found")
        }
    }

    object Adapter : JsonAdapter<EthMethod> {
        override fun serialize(p0: EthMethod, p1: Type?, p2: JsonSerializationContext?): JsonElement {
            return JsonPrimitive(p0.value)
        }

        override fun deserialize(p0: JsonElement?, p1: Type?, p2: JsonDeserializationContext?): EthMethod {
            return EthMethod.values().find { method -> method.value == p0!!.asString }!!
        }

    }
}
