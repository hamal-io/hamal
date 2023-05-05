package io.hamal.lib.ddd.base

import io.hamal.lib.Shard
import kotlin.reflect.KClass

abstract class DomainNotification {

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
