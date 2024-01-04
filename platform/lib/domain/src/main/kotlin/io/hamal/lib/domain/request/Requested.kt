package io.hamal.lib.domain.request

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import io.hamal.lib.common.serialization.GsonSerde
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.vo.RequestId
import io.hamal.lib.domain.vo.RequestType
import java.lang.reflect.Type

sealed class Requested {
    abstract val id: RequestId
    abstract var status: RequestStatus
    val type: RequestType = RequestType(this::class.java.simpleName)
}

class RequestedTypeAdapter : GsonSerde<Requested> {
    override fun serialize(submitted: Requested, type: Type, ctx: JsonSerializationContext): JsonElement {
        return ctx.serialize(submitted)
    }

    override fun deserialize(element: JsonElement, type: Type, ctx: JsonDeserializationContext): Requested {
        val requestedType = element.asJsonObject.get("type").asString
        return requestedClassesMapping[requestedType]?.let {
            ctx.deserialize(element, it.java)
        } ?: TODO()
    }

    private val requestedClasses = listOf(
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
        ExecInvokeRequested::class,
        ExtensionCreateRequested::class,
        ExtensionUpdateRequested::class,
        FuncCreateRequested::class,
        FuncDeployRequested::class,
        FuncUpdateRequested::class,
        HookCreateRequested::class,
        HookInvokeRequested::class,
        HookUpdateRequested::class,
        FlowCreateRequested::class,
        FlowUpdateRequested::class,
        StateSetRequested::class,
        TopicAppendToRequested::class,
        TopicCreateRequested::class,
        TriggerCreateRequested::class,
        TriggerStatusRequested::class,
    )

    private val requestedClassesMapping = requestedClasses.associateBy { it.simpleName!! }
}