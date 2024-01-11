package io.hamal.lib.sdk.api

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import io.hamal.lib.common.serialization.JsonAdapter
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.vo.RequestId
import io.hamal.lib.domain.vo.RequestType

data class ApiRequestList(
    val requests: List<ApiRequested>
) : ApiObject()

sealed class ApiRequested {
    abstract val id: RequestId
    abstract val status: RequestStatus
    val type: RequestType = RequestType(this::class.java.simpleName)

    object Adapter : JsonAdapter<ApiRequested> {
        override fun serialize(
            src: ApiRequested,
            typeOfSrc: java.lang.reflect.Type,
            context: JsonSerializationContext
        ): JsonElement {
            return context.serialize(src)
        }

        override fun deserialize(
            json: JsonElement,
            typeOfT: java.lang.reflect.Type,
            context: JsonDeserializationContext
        ): ApiRequested {
            val requestType = json.asJsonObject.get("type").asString
            return context.deserialize(
                json, (classMapping[requestType]
                    ?: throw NotImplementedError("$requestType not supported")).java
            )
        }

        private val classMapping = listOf(
            ApiAccountConvertRequested::class,
            ApiBlueprintCreateRequested::class,
            ApiBlueprintUpdateRequested::class,
            ApiExecInvokeRequested::class,
            ApiEndpointCreateRequested::class,
            ApiEndpointUpdateRequested::class,
            ApiExtensionCreateRequested::class,
            ApiExtensionUpdateRequested::class,
            ApiFlowCreateRequested::class,
            ApiFlowUpdateRequested::class,
            ApiFuncCreateRequested::class,
            ApiFuncDeployRequested::class,
            ApiFuncUpdateRequested::class,
            ApiHookCreateRequested::class,
            ApiHookUpdateRequested::class,
            ApiTopicAppendRequested::class,
            ApiTopicCreateRequested::class,
            ApiTriggerCreateRequested::class,
            ApiTriggerStatusRequested::class
        ).associateBy { it.simpleName }

    }
}
