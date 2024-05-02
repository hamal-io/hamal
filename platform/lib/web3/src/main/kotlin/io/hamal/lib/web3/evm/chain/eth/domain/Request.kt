package io.hamal.lib.web3.evm.chain.eth.domain

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import io.hamal.lib.common.serialization.GsonTransform
import io.hamal.lib.common.serialization.JsonAdapter
import io.hamal.lib.common.serialization.serde.SerdeArray
import io.hamal.lib.common.serialization.serde.SerdeNull
import io.hamal.lib.common.serialization.serde.SerdeObject
import io.hamal.lib.common.serialization.serde.SerdeString
import io.hamal.lib.domain.Json
import io.hamal.lib.web3.evm.abi.type.EvmAddress
import io.hamal.lib.web3.evm.abi.type.EvmPrefixedHexString
import io.hamal.lib.web3.evm.abi.type.EvmUint64
import io.hamal.lib.web3.evm.domain.EvmMethod
import io.hamal.lib.web3.evm.domain.EvmMethod.Call
import io.hamal.lib.web3.evm.domain.EvmMethod.GetBlockByNumber
import io.hamal.lib.web3.evm.domain.EvmRequest
import io.hamal.lib.web3.evm.domain.EvmRequestId
import java.lang.reflect.Type


sealed interface EthRequest : EvmRequest {

    override val id: EvmRequestId
    override val method: EvmMethod

    object Adapter : JsonAdapter<EthRequest> {
        override fun serialize(request: EthRequest, type: Type, ctx: JsonSerializationContext): JsonElement {
            return when (request) {
                is EthGetBlockByNumberRequest -> GsonTransform.fromNode(
                    SerdeObject.builder()
                        .set("id", GsonTransform.toNode(ctx.serialize(request.id)))
                        .set("method", GsonTransform.toNode(ctx.serialize(request.method)))
                        .set(
                            "params", SerdeArray.builder()
                                .append(SerdeString(request.number.toPrefixedHexString().value))
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
            return when (EvmMethod.of(obj.get("method").asString)) {
                GetBlockByNumber -> EthGetBlockByNumberRequest(
                    id = EvmRequestId(obj.get("id").asString),
                    number = EvmUint64(EvmPrefixedHexString(obj.getAsJsonArray("params").get(0).asString)),
                    fullTransaction = obj.getAsJsonArray("params").get(1).asBoolean
                )

                else -> TODO()
            }
        }
    }
}

data class EthCallRequest(
    override val id: EvmRequestId,
    val from: EvmAddress?,
    val to: EvmAddress,
    val data: EvmPrefixedHexString,
    val number: EvmUint64,
) : EthRequest {
    override val method: EvmMethod = Call

    object Adapter : JsonAdapter<EthCallRequest> {
        override fun serialize(request: EthCallRequest, typeOfSrc: Type, ctx: JsonSerializationContext): JsonElement {
            return GsonTransform.fromNode(
                SerdeObject.builder()
                    .set("id", GsonTransform.toNode(ctx.serialize(request.id)))
                    .set("method", GsonTransform.toNode(ctx.serialize(request.method)))
                    .set(
                        "params", SerdeArray.builder()
                            .append(
                                SerdeObject.builder()
                                    .set("from", request.from?.toPrefixedHexString()?.value?.let(::SerdeString) ?: SerdeNull)
                                    .set("to", request.to.toPrefixedHexString().value)
                                    .set("data", request.data.value)
                                    .build()
                            )
                            .append(SerdeString(request.number.toPrefixedHexString().value))
                            .build()
                    )
                    .build()
            )
        }

        override fun deserialize(element: JsonElement, typeOfT: Type, context: JsonDeserializationContext): EthCallRequest {
            val obj = element.asJsonObject
            return EthCallRequest(
                id = EvmRequestId(obj.get("id").asString),
                data = EvmPrefixedHexString(obj.getAsJsonArray("params").get(0).asJsonObject.get("data").asString),
                from = obj.getAsJsonArray("params").get(0).asJsonObject.get("from")?.asString?.let(::EvmAddress),
                to = EvmAddress(obj.getAsJsonArray("params").get(0).asJsonObject.get("to").asString),
                number = EvmUint64(EvmPrefixedHexString(obj.getAsJsonArray("params").get(0).asString)),
            )
        }
    }
}


data class EthGetBlockByNumberRequest(
    override val id: EvmRequestId,
    val number: EvmUint64,
    val fullTransaction: Boolean
) : EthRequest {
    override val method: EvmMethod = GetBlockByNumber

    object Adapter : JsonAdapter<EthGetBlockByNumberRequest> {
        override fun serialize(request: EthGetBlockByNumberRequest, type: Type, ctx: JsonSerializationContext): JsonElement {
            return GsonTransform.fromNode(
                SerdeObject.builder()
                    .set("id", GsonTransform.toNode(ctx.serialize(request.id)))
                    .set("method", GsonTransform.toNode(ctx.serialize(request.method)))
                    .set(
                        "params", SerdeArray.builder()
                            .append(SerdeString(request.number.toPrefixedHexString().value))
                            .append(request.fullTransaction)
                            .build()
                    )
                    .build()
            )
        }

        override fun deserialize(element: JsonElement, type: Type, ctx: JsonDeserializationContext): EthGetBlockByNumberRequest {
            val obj = element.asJsonObject
            return EthGetBlockByNumberRequest(
                id = EvmRequestId(obj.get("id").asString),
                number = EvmUint64(EvmPrefixedHexString(obj.getAsJsonArray("params").get(0).asString)),
                fullTransaction = obj.getAsJsonArray("params").get(1).asBoolean
            )
        }
    }
}

fun parseEthRequest(json: Json, request: SerdeObject): Pair<EthErrorResponse?, EthRequest?> {
    return try {
        val ethRequest = json.deserialize(EthRequest::class, json.serialize(request))
        null to ethRequest
    } catch (e: Throwable) {
        e.printStackTrace()
        when {
            e.message?.contains("does not start with 0x") == true -> {
                EthErrorResponse(
                    id = EvmRequestId(request["id"].stringValue),
                    error = EthError(EthError.ErrorCode.InvalidParams, "invalid argument: hex string without 0x prefix")
                ) to null
            }

            e.message?.contains("does not match hex pattern") == true -> {
                EthErrorResponse(
                    id = EvmRequestId(request["id"].stringValue),
                    error = EthError(EthError.ErrorCode.InvalidParams, "invalid argument: hex string")
                ) to null
            }

            e.message?.contains("out of bounds for length") == true -> {
                EthErrorResponse(
                    id = EvmRequestId(request["id"].stringValue),
                    error = EthError(EthError.ErrorCode.InvalidParams, "missing argument")
                ) to null
            }

            e.message?.contains("EthMethod not found") == true -> {
                EthErrorResponse(
                    id = EvmRequestId(request["id"].stringValue),
                    error = EthError(EthError.ErrorCode.MethodNotFound, "method not supported")
                ) to null
            }

            else -> EthErrorResponse(
                id = EvmRequestId(request["id"].stringValue),
                error = EthError(EthError.ErrorCode.InternalError, "Unexpected error")
            ) to null
        }
    }
}