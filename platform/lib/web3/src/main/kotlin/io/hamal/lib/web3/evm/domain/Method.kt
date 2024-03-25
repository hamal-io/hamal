package io.hamal.lib.web3.evm.domain

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import io.hamal.lib.common.serialization.JsonAdapter
import java.lang.reflect.Type


enum class EvmMethod(val value: String) {
    Call("eth_call"),
    GetBlockByNumber("eth_getBlockByNumber");

    companion object {
        fun of(value: String): EvmMethod {
            return entries.find { it.value == value } ?: throw NoSuchElementException("EthMethod not found")
        }
    }

    object Adapter : JsonAdapter<EvmMethod> {
        override fun serialize(p0: EvmMethod, p1: Type?, p2: JsonSerializationContext?): JsonElement {
            return JsonPrimitive(p0.value)
        }

        override fun deserialize(p0: JsonElement?, p1: Type?, p2: JsonDeserializationContext?): EvmMethod {
            return EvmMethod.values().find { method -> method.value == p0!!.asString }!!
        }

    }
}
