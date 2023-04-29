package io.hamal.backend.core.notification

import io.hamal.lib.vo.Shard
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass

@Serializable
sealed class DomainNotification {
    abstract val shard: Shard

    val topic: String

    init {
        val topicAnnotation =
            this::class.annotations.find { annotation -> annotation.annotationClass == DomainNotificationTopic::class }
                ?: throw IllegalStateException("DomainNotification not annotated with @DomainNotificationTopic")
        topic = (topicAnnotation as DomainNotificationTopic).value
    }

    override fun toString(): String {
        return "${this::class.qualifiedName}"
    }
}

@MustBeDocumented
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class DomainNotificationTopic(val value: String)


fun <NOTIFICATION : DomainNotification> KClass<NOTIFICATION>.topic() =
    annotations.find { annotation -> annotation.annotationClass == DomainNotificationTopic::class }
        .let { it as DomainNotificationTopic }.value
