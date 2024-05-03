package io.hamal.lib.web3.evm.chain.arbitrum.domain

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import io.hamal.lib.common.serialization.GsonTransform
import io.hamal.lib.common.serialization.AdapterJson
import io.hamal.lib.common.serialization.SerdeJson
import io.hamal.lib.common.serialization.json.JsonArray
import io.hamal.lib.common.serialization.json.JsonObject
import io.hamal.lib.common.serialization.json.JsonString
import io.hamal.lib.web3.evm.abi.type.EvmPrefixedHexString
import io.hamal.lib.web3.evm.abi.type.EvmUint64
import io.hamal.lib.web3.evm.domain.EvmMethod
import io.hamal.lib.web3.evm.domain.EvmMethod.GetBlockByNumber
import io.hamal.lib.web3.evm.domain.EvmRequest
import io.hamal.lib.web3.evm.domain.EvmRequestId
import java.lang.reflect.Type


sealed interface ArbitrumRequest : EvmRequest {

    override val id: EvmRequestId
    override val method: EvmMethod

    object Adapter : AdapterJson<ArbitrumRequest> {
        override fun serialize(request: ArbitrumRequest, type: Type, ctx: JsonSerializationContext): JsonElement {
            return when (request) {
                is ArbitrumGetBlockByNumberRequest -> GsonTransform.fromNode(
                    JsonObject.builder()
                        .set("id", GsonTransform.toNode(ctx.serialize(request.id)))
                        .set("method", GsonTransform.toNode(ctx.serialize(request.method)))
                        .set(
                            "params", JsonArray.builder()
                                .append(JsonString(request.number.toPrefixedHexString().value))
                                .append(request.fullTransaction)
                                .build()
                        )
                        .build()
                )

                else -> TODO()
            }
        }

        override fun deserialize(element: JsonElement, type: Type, ctx: JsonDeserializationContext): ArbitrumRequest {
            val obj = element.asJsonObject
            return when (EvmMethod.of(obj.get("method").asString)) {
                GetBlockByNumber -> ArbitrumGetBlockByNumberRequest(
                    id = EvmRequestId(obj.get("id").asString),
                    number = EvmUint64(EvmPrefixedHexString(obj.getAsJsonArray("params").get(0).asString)),
                    fullTransaction = obj.getAsJsonArray("params").get(1).asBoolean
                )

                else -> TODO()
            }
        }
    }
}

data class ArbitrumGetBlockByNumberRequest(
    override val id: EvmRequestId,
    val number: EvmUint64,
    val fullTransaction: Boolean
) : ArbitrumRequest {
    override val method: EvmMethod = GetBlockByNumber

    object Adapter : AdapterJson<ArbitrumGetBlockByNumberRequest> {
        override fun serialize(request: ArbitrumGetBlockByNumberRequest, type: Type, ctx: JsonSerializationContext): JsonElement {
            return GsonTransform.fromNode(
                JsonObject.builder()
                    .set("id", GsonTransform.toNode(ctx.serialize(request.id)))
                    .set("method", GsonTransform.toNode(ctx.serialize(request.method)))
                    .set(
                        "params", JsonArray.builder()
                            .append(JsonString(request.number.toPrefixedHexString().value))
                            .append(request.fullTransaction)
                            .build()
                    )
                    .build()
            )
        }

        override fun deserialize(element: JsonElement, type: Type, ctx: JsonDeserializationContext): ArbitrumGetBlockByNumberRequest {
            val obj = element.asJsonObject
            return ArbitrumGetBlockByNumberRequest(
                id = EvmRequestId(obj.get("id").asString),
                number = EvmUint64(EvmPrefixedHexString(obj.getAsJsonArray("params").get(0).asString)),
                fullTransaction = obj.getAsJsonArray("params").get(1).asBoolean
            )
        }
    }
}

fun parseArbitrumRequest(serde: SerdeJson, request: JsonObject): Pair<ArbitrumErrorResponse?, ArbitrumRequest?> {
    return try {
        val ethRequest = serde.read(ArbitrumRequest::class, serde.write(request))
        null to ethRequest
    } catch (e: Throwable) {
        e.printStackTrace()
        when {
            e.message?.contains("does not start with 0x") == true -> {
                ArbitrumErrorResponse(
                    id = EvmRequestId(request["id"].stringValue),
                    error = ArbitrumError(ArbitrumError.ErrorCode.InvalidParams, "invalid argument: hex string without 0x prefix")
                ) to null
            }

            e.message?.contains("does not match hex pattern") == true -> {
                ArbitrumErrorResponse(
                    id = EvmRequestId(request["id"].stringValue),
                    error = ArbitrumError(ArbitrumError.ErrorCode.InvalidParams, "invalid argument: hex string")
                ) to null
            }

            e.message?.contains("out of bounds for length") == true -> {
                ArbitrumErrorResponse(
                    id = EvmRequestId(request["id"].stringValue),
                    error = ArbitrumError(ArbitrumError.ErrorCode.InvalidParams, "missing argument")
                ) to null
            }

            e.message?.contains("EthMethod not found") == true -> {
                ArbitrumErrorResponse(
                    id = EvmRequestId(request["id"].stringValue),
                    error = ArbitrumError(ArbitrumError.ErrorCode.MethodNotFound, "method not supported")
                ) to null
            }

            else -> ArbitrumErrorResponse(
                id = EvmRequestId(request["id"].stringValue),
                error = ArbitrumError(ArbitrumError.ErrorCode.InternalError, "Unexpected error")
            ) to null
        }
    }
}