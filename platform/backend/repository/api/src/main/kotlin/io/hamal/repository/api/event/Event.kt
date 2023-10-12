package io.hamal.repository.api.event

import io.hamal.lib.domain.vo.TopicName
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass

@Serializable
sealed class PlatformEvent {
    val topicName get() = this::class.topicName()
}

fun <EVENT : PlatformEvent> KClass<EVENT>.topicName() =
    (annotations.find { annotation -> annotation.annotationClass == PlatformEventTopic::class }
        ?: throw IllegalStateException("PlatformEvent not annotated with @PlatformEventTopic"))
        .let { TopicName((it as PlatformEventTopic).value) }

@MustBeDocumented
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class PlatformEventTopic(val value: String)