package io.hamal.backend.event

import io.hamal.lib.domain.Shard
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass

@Serializable
sealed class Event {
    abstract val shard: Shard

    val topic: String by lazy {
        val topicAnnotation =
            this::class.annotations.find { annotation -> annotation.annotationClass == DomainNotificationTopic::class }
                ?: throw IllegalStateException("DomainNotification not annotated with @DomainNotificationTopic")
        (topicAnnotation as DomainNotificationTopic).value
    }
}


@MustBeDocumented
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class DomainNotificationTopic(val value: String)


fun <NOTIFICATION : Event> KClass<NOTIFICATION>.topic() =
    annotations.find { annotation -> annotation.annotationClass == DomainNotificationTopic::class }
        .let { it as DomainNotificationTopic }.value
