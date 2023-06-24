package io.hamal.backend.instance.event

import io.hamal.lib.domain.vo.Content
import io.hamal.lib.domain.vo.ContentType
import io.hamal.lib.domain.vo.TopicName
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass

@Serializable
sealed class SystemEvent {
    val topic = TopicName(this::class.topic())
}

fun <EVENT : SystemEvent> KClass<EVENT>.topic() =
    (annotations.find { annotation -> annotation.annotationClass == SystemEventTopic::class }
        ?: throw IllegalStateException("SystemEvent not annotated with @SystemEventTopic"))
        .let { it as SystemEventTopic }.value

@MustBeDocumented
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class SystemEventTopic(val value: String)


@Serializable
data class Event(
    val contentType: ContentType,
    val content: Content
)