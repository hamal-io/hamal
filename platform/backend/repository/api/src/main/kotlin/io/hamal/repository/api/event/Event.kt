package io.hamal.repository.api.event

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import io.hamal.lib.common.domain.ValueObjectString
import io.hamal.lib.common.serialization.JsonAdapter
import io.hamal.lib.domain.vo.TopicName
import kotlin.reflect.KClass

class PlatformEventClass(override val value: String) : ValueObjectString()

sealed class PlatformEvent {
    val `class`: PlatformEventClass = PlatformEventClass(this::class.simpleName!!)
    val topicName get() = this::class.topicName()

    object Adapter : JsonAdapter<PlatformEvent> {
        override fun serialize(
            src: PlatformEvent,
            typeOfSrc: java.lang.reflect.Type,
            context: JsonSerializationContext
        ): JsonElement {
            return context.serialize(src)
        }

        override fun deserialize(
            json: JsonElement,
            typeOfT: java.lang.reflect.Type,
            context: JsonDeserializationContext
        ): PlatformEvent {
            val eventClass = json.asJsonObject.get("class").asString
            return context.deserialize(
                json, (classMapping[eventClass]
                    ?: throw NotImplementedError("$eventClass not supported")).java
            )
        }

        private val classMapping = listOf(
            AccountCreatedEvent::class,
            AccountConvertedEvent::class,
            BlueprintCreatedEvent::class,
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
            FlowCreatedEvent::class,
            FlowUpdatedEvent::class,
            FuncCreatedEvent::class,
            FuncUpdatedEvent::class,
            FuncDeployedEvent::class,
            GroupCreatedEvent::class,
            HookCreatedEvent::class,
            HookUpdatedEvent::class,
            StateUpdatedEvent::class,
            TriggerCreatedEvent::class,
            TriggerActivatedEvent::class,
            TriggerDeactivatedEvent::class
        ).associateBy { it.simpleName }

    }
}

fun <EVENT : PlatformEvent> KClass<EVENT>.topicName() = TopicName(this.java.simpleName)
