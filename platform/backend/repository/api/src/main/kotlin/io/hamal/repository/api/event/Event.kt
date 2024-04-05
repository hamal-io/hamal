package io.hamal.repository.api.event

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import io.hamal.lib.common.domain.ValueObjectString
import io.hamal.lib.common.serialization.JsonAdapter
import io.hamal.lib.domain.vo.TopicName
import kotlin.reflect.KClass

class InternalEventClass(override val value: String) : ValueObjectString()

val internalEventClasses = listOf(
    AccountCreatedEvent::class,
    AccountConvertedEvent::class,
    AccountUpdatedEvent::class,
    BlueprintCreatedEvent::class,
    BlueprintUpdatedEvent::class,
    EndpointCreatedEvent::class,
    EndpointUpdatedEvent::class,
    ExecPlannedEvent::class,
    ExecScheduledEvent::class,
    ExecQueuedEvent::class,
    ExecStartedEvent::class,
    ExecCompletedEvent::class,
    ExecFailedEvent::class,
    ExtensionCreatedEvent::class,
    ExtensionUpdatedEvent::class,
    FeedbackCreatedEvent::class,
    NamespaceAppendedEvent::class,
    NamespaceUpdatedEvent::class,
    FuncCreatedEvent::class,
    FuncUpdatedEvent::class,
    FuncDeployedEvent::class,
    WorkspaceCreatedEvent::class,
    HookCreatedEvent::class,
    HookUpdatedEvent::class,
    StateUpdatedEvent::class,
    TopicCreatedEvent::class,
    TriggerCreatedEvent::class,
    TriggerActivatedEvent::class,
    TriggerDeactivatedEvent::class
)

sealed class InternalEvent {
    val `class`: InternalEventClass = InternalEventClass(this::class.simpleName!!)
    val topicName get() = this::class.topicName()

    object Adapter : JsonAdapter<InternalEvent> {
        override fun serialize(
            src: InternalEvent,
            typeOfSrc: java.lang.reflect.Type,
            context: JsonSerializationContext
        ): JsonElement {
            return context.serialize(src)
        }

        override fun deserialize(
            json: JsonElement,
            typeOfT: java.lang.reflect.Type,
            context: JsonDeserializationContext
        ): InternalEvent {
            val eventClass = json.asJsonObject.get("class").asString
            return context.deserialize(
                json, (classMapping[eventClass]
                    ?: throw NotImplementedError("$eventClass not supported")).java
            )
        }

        private val classMapping = internalEventClasses.associateBy { it.simpleName }
    }
}

fun <EVENT : InternalEvent> KClass<EVENT>.topicName() = TopicName(this.java.simpleName)
