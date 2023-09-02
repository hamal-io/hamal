package io.hamal.backend.event.event

import io.hamal.lib.domain.vo.TopicName
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass

@Serializable
sealed class HubEvent {
    val topicName get() = this::class.topicName()
}

fun <EVENT : HubEvent> KClass<EVENT>.topicName() =
    (annotations.find { annotation -> annotation.annotationClass == HubEventTopic::class }
        ?: throw IllegalStateException("HubEvent not annotated with @HubEventTopic"))
        .let { TopicName((it as HubEventTopic).value) }

@MustBeDocumented
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class HubEventTopic(val value: String)