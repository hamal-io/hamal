package io.hamal.repository.api.event

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import io.hamal.lib.common.domain.ValueObjectString
import io.hamal.lib.common.serialization.JsonAdapter
import io.hamal.lib.domain.vo.TopicName
import kotlin.reflect.KClass

class PlatformEventType(override val value: String) : ValueObjectString()

sealed class PlatformEvent {
    val topicName get() = this::class.topicName()

    val type: PlatformEventType = PlatformEventType(this::class.simpleName!!)

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
            val type = json.asJsonObject.get("type").asString
            return context.deserialize(
                json, (classMapping[type]
                    ?: throw NotImplementedError("$type not supported")).java
            )
        }

        private val classMapping = listOf(
            AccountCreatedEvent::class,
            AccountConvertedEvent::class,
            BlueprintCreatedEvent::class,
            ExecPlannedEvent::class,
            ExecScheduledEvent::class,
            ExecQueuedEvent::class,
            ExecStartedEvent::class,
            ExecCompletedEvent::class,
            ExecFailedEvent::class,
            FlowCreatedEvent::class,
            FlowUpdatedEvent::class,
            HookCreatedEvent::class,
            HookUpdatedEvent::class,
            TriggerCreatedEvent::class,
            TriggerActivatedEvent::class,
            TriggerDeactivatedEvent::class
        ).associateBy { it.simpleName }

    }
}

fun <EVENT : PlatformEvent> KClass<EVENT>.topicName() =
    (annotations.find { annotation -> annotation.annotationClass == PlatformEventTopic::class }
        ?: throw IllegalStateException("PlatformEvent not annotated with @PlatformEventTopic"))
        .let { TopicName((it as PlatformEventTopic).value) }

@MustBeDocumented
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class PlatformEventTopic(val value: String)