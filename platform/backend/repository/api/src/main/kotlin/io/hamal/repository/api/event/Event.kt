package io.hamal.repository.api.event

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import io.hamal.lib.common.serialization.AdapterJson
import io.hamal.lib.common.value.ValueString
import io.hamal.lib.common.value.ValueVariableString
import io.hamal.lib.domain.vo.TopicName.Companion.TopicName
import io.hamal.repository.api.event.InternalEventClass.Companion.InternalEventClass
import kotlin.reflect.KClass

class InternalEventClass(override val value: ValueString) : ValueVariableString() {
    companion object {
        fun InternalEventClass(value: String) = InternalEventClass(ValueString(value))
    }
}

val internalEventClasses = listOf(
    AccountCreatedEvent::class,
    AccountConvertedEvent::class,
    AuthUpdatedEvent::class,
    RecipeCreatedEvent::class,
    RecipeUpdatedEvent::class,
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
    StateUpdatedEvent::class,
    TopicCreatedEvent::class,
    TriggerCreatedEvent::class,
    TriggerActivatedEvent::class,
    TriggerDeactivatedEvent::class
)

sealed class InternalEvent {
    val `class`: InternalEventClass = InternalEventClass(this::class.simpleName!!)
    val topicName get() = this::class.topicName()

    object Adapter : AdapterJson<InternalEvent> {
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
