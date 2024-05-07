package io.hamal.lib.domain.request

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import io.hamal.lib.common.serialization.AdapterGeneric
import io.hamal.lib.domain.vo.AuthId
import io.hamal.lib.domain.vo.RequestClass
import io.hamal.lib.domain.vo.RequestClass.Companion.RequestClass
import io.hamal.lib.domain.vo.RequestId
import io.hamal.lib.domain.vo.RequestStatus
import java.lang.reflect.Type


sealed class Requested {
    abstract val requestId: RequestId
    abstract val requestedBy: AuthId
    abstract val requestStatus: RequestStatus
    val `class`: RequestClass = RequestClass(this::class.java.simpleName)

    object Adapter : AdapterGeneric<Requested> {
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

            val requestClass = context.deserialize<RequestClass>(
                json.asJsonObject.get("class"), RequestClass::class.java
            )

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
            AuthUpdatePasswordRequested::class,
            ExecCompleteRequested::class,
            ExecFailRequested::class,
            ExecInvokeRequested::class,
            ExtensionCreateRequested::class,
            ExtensionUpdateRequested::class,
            FeedbackCreateRequested::class,
            FuncCreateRequested::class,
            FuncDeployRequested::class,
            FuncUpdateRequested::class,
            NamespaceAppendRequested::class,
            NamespaceUpdateRequested::class,
            RecipeCreateRequested::class,
            RecipeUpdateRequested::class,
            StateSetRequested::class,
            TestRequested::class,
            TopicAppendEventRequested::class,
            TopicCreateRequested::class,
            TriggerCreateRequested::class,
            TriggerStatusRequested::class,
        ).associateBy { RequestClass(it.simpleName!!) }
    }
}