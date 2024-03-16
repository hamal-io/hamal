package io.hamal.lib.web3.eth.domain

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import io.hamal.lib.common.hot.HotArray
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.common.hot.HotString
import io.hamal.lib.common.serialization.GsonTransform
import io.hamal.lib.common.serialization.JsonAdapter
import io.hamal.lib.domain.Json
import io.hamal.lib.web3.eth.abi.type.EthPrefixedHexString
import io.hamal.lib.web3.eth.abi.type.EthUint64
import io.hamal.lib.web3.eth.domain.EthMethod.GetBlockByNumber
import java.lang.reflect.Type


data class EthRequestId(val value: String) {
    object Adapter : JsonAdapter<EthRequestId> {

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): EthRequestId {
            return EthRequestId(json.asString)
        }

        override fun serialize(src: EthRequestId, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return JsonPrimitive(src.value)
        }
    }
}

sealed interface EthRequest {
    val id: EthRequestId
    val method: EthMethod

    object Adapter : JsonAdapter<EthRequest> {
        override fun serialize(request: EthRequest, type: Type, ctx: JsonSerializationContext): JsonElement {
            return when (request) {
                is EthGetBlockByNumberRequest -> GsonTransform.fromNode(
                    HotObject.builder()
                        .set("id", GsonTransform.toNode(ctx.serialize(request.id)))
                        .set("method", GsonTransform.toNode(ctx.serialize(request.method)))
                        .set(
                            "params", HotArray.builder()
                                .append(HotString(request.number.toPrefixedHexString().value))
                                .append(request.fullTransaction)
                                .build()
                        )
                        .build()
                )

                else -> TODO()
            }
        }

        override fun deserialize(element: JsonElement, type: Type, ctx: JsonDeserializationContext): EthRequest {
            val obj = element.asJsonObject
            return when (EthMethod.of(obj.get("method").asString)) {
                GetBlockByNumber -> EthGetBlockByNumberRequest(
                    id = EthRequestId(obj.get("id").asString),
                    number = EthUint64(EthPrefixedHexString(obj.getAsJsonArray("params").get(0).asString)),
                    fullTransaction = obj.getAsJsonArray("params").get(1).asBoolean
                )

                else -> TODO()
            }
        }
    }
}

data class EthGetBlockByNumberRequest(
    override val id: EthRequestId,
    val number: EthUint64,
    val fullTransaction: Boolean
) : EthRequest {
    override val method: EthMethod = GetBlockByNumber

    object Adapter : JsonAdapter<EthGetBlockByNumberRequest> {
        override fun serialize(request: EthGetBlockByNumberRequest, type: Type, ctx: JsonSerializationContext): JsonElement {
            return GsonTransform.fromNode(
                HotObject.builder()
                    .set("id", GsonTransform.toNode(ctx.serialize(request.id)))
                    .set("method", GsonTransform.toNode(ctx.serialize(request.method)))
                    .set(
                        "params", HotArray.builder()
                            .append(HotString(request.number.toPrefixedHexString().value))
                            .append(request.fullTransaction)
                            .build()
                    )
                    .build()
            )
        }

        override fun deserialize(element: JsonElement, type: Type, ctx: JsonDeserializationContext): EthGetBlockByNumberRequest {
            val obj = element.asJsonObject
            return EthGetBlockByNumberRequest(
                id = EthRequestId(obj.get("id").asString),
                number = EthUint64(EthPrefixedHexString(obj.getAsJsonArray("params").get(0).asString)),
                fullTransaction = obj.getAsJsonArray("params").get(1).asBoolean
            )
        }
    }
}

fun parseRequest(json: Json, request: HotObject): Pair<EthErrorResponse?, EthRequest?> {
    return try {
        val ethRequest = json.deserialize(EthRequest::class, json.serialize(request))
        null to ethRequest
    } catch (e: Throwable) {
        e.printStackTrace()
        when {
            e.message?.contains("does not start with 0x") == true -> {
                EthErrorResponse(
                    id = EthRequestId(request["id"].stringValue),
                    error = EthError(EthError.ErrorCode.InvalidParams, "invalid argument: hex string without 0x prefix")
                ) to null
            }

            e.message?.contains("does not match hex pattern") == true -> {
                EthErrorResponse(
                    id = EthRequestId(request["id"].stringValue),
                    error = EthError(EthError.ErrorCode.InvalidParams, "invalid argument: hex string")
                ) to null
            }

            e.message?.contains("out of bounds for length") == true -> {
                EthErrorResponse(
                    id = EthRequestId(request["id"].stringValue),
                    error = EthError(EthError.ErrorCode.InvalidParams, "missing argument")
                ) to null
            }

            e.message?.contains("EthMethod not found") == true -> {
                EthErrorResponse(
                    id = EthRequestId(request["id"].stringValue),
                    error = EthError(EthError.ErrorCode.MethodNotFound, "method not supported")
                ) to null
            }

            else -> EthErrorResponse(
                id = EthRequestId(request["id"].stringValue),
                error = EthError(EthError.ErrorCode.InternalError, "Unexpected error")
            ) to null
        }
    }
}