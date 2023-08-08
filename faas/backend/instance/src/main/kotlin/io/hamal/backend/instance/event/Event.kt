package io.hamal.backend.instance.event

import io.hamal.lib.domain.vo.TopicName
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass

@Serializable
sealed class SystemEvent {
    val topic = TopicName(this::class.topic())
}

fun <EVENT : SystemEvent> KClass<EVENT>.topic() =
    (annotations.find { annotation -> annotation.annotationClass == SystemTopic::class }
        ?: throw IllegalStateException("SystemEvent not annotated with @SystemEventTopic"))
        .let { it as SystemTopic }.value

@MustBeDocumented
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class SystemTopic(val value: String)