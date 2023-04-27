package io.hamal.lib.domain_notification.notification

import io.hamal.lib.domain.vo.RegionId
import io.hamal.lib.domain_notification.DomainNotificationTopic
import kotlinx.serialization.Serializable

@Serializable
sealed class DomainNotification {
    abstract val regionId: RegionId

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