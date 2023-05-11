package io.hamal.backend.core.notification

import io.hamal.lib.domain.Shard
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass

@Serializable
sealed class DomainNotification {
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


fun <NOTIFICATION : DomainNotification> KClass<NOTIFICATION>.topic() =
    annotations.find { annotation -> annotation.annotationClass == DomainNotificationTopic::class }
        .let { it as DomainNotificationTopic }.value
