package io.hamal.lib.domain.request

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import io.hamal.lib.common.serialization.JsonAdapter
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.vo.RequestClass
import io.hamal.lib.domain.vo.RequestId
import java.lang.reflect.Type


sealed class Requested {
    abstract val id: RequestId
//    abstract val by: AuthId
    abstract var status: RequestStatus
    val `class`: RequestClass = RequestClass(this::class.java.simpleName)

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
            val requestClass = json.asJsonObject.get("class").asString
            return context.deserialize(json, classMapping[requestClass]!!.java)
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
            NamespaceAppendRequested::class,
            NamespaceUpdateRequested::class,
            StateSetRequested::class,
            TestRequested::class,
            TopicAppendEventRequested::class,
            TopicCreateRequested::class,
            TriggerCreateRequested::class,
            TriggerStatusRequested::class,
        ).associateBy { it.simpleName }
    }
}