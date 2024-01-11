package io.hamal.lib.domain.request

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import io.hamal.lib.common.serialization.JsonAdapter
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.vo.RequestId
import io.hamal.lib.domain.vo.RequestType
import java.lang.reflect.Type

sealed class Requested {
    abstract val id: RequestId
    abstract var status: RequestStatus
    val type: RequestType = RequestType(this::class.java.simpleName)

    object Adapter : JsonAdapter<Requested> {
        override fun serialize(
            src: Requested,
            typeOfSrc: Type,
            context: JsonSerializationContext
        ): JsonElement {
            return context.serialize(src)
        }

        override fun deserialize(
            json: JsonElement,
            typeOfT: Type,
            context: JsonDeserializationContext
        ): Requested {
            val type = json.asJsonObject.get("type").asString
            return context.deserialize(json, classMapping[type]!!.java)
        }

        private val classMapping = listOf(
            AccountCreateAnonymousRequested::class,
            AccountCreateRequested::class,
            AccountCreateMetaMaskRequested::class,
            AccountConvertRequested::class,
            AuthLoginMetaMaskRequested::class,
            AuthLoginEmailRequested::class,
            AuthLogoutRequested::class,
            BlueprintCreateRequested::class,
            BlueprintUpdateRequested::class,
            EndpointCreateRequested::class,
            EndpointUpdateRequested::class,
            ExecCompleteRequested::class,
            ExecFailRequested::class,
            ExecInvokeRequested::class,
            ExtensionCreateRequested::class,
            ExtensionUpdateRequested::class,
            FeedbackCreateRequested::class,
            FuncCreateRequested::class,
            FuncDeployRequested::class,
            FuncUpdateRequested::class,
            HookCreateRequested::class,
            HookInvokeRequested::class,
            HookUpdateRequested::class,
            FlowCreateRequested::class,
            FlowUpdateRequested::class,
            StateSetRequested::class,
            TestRequested::class,
            TopicAppendToRequested::class,
            TopicCreateRequested::class,
            TriggerCreateRequested::class,
            TriggerStatusRequested::class,
        ).associateBy { it.simpleName }
    }
}