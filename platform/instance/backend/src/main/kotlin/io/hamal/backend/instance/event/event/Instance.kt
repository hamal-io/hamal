package io.hamal.backend.instance.event.event

import io.hamal.lib.domain.vo.TopicName
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass

@Serializable
sealed class InstanceEvent {
    val topicName get() = this::class.topicName()
}

fun <EVENT : InstanceEvent> KClass<EVENT>.topicName() =
    (annotations.find { annotation -> annotation.annotationClass == InstanceEventTopic::class }
        ?: throw IllegalStateException("InstanceEvent not annotated with @InstanceEventTopic"))
        .let { TopicName((it as InstanceEventTopic).value) }

@MustBeDocumented
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class InstanceEventTopic(val value: String)