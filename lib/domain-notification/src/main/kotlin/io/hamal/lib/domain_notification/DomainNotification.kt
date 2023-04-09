package io.hamal.lib.domain_notification

import io.hamal.lib.domain.vo.base.RegionId
import io.hamal.lib.meta.exception.IllegalStateException

abstract class DomainNotification(
    val regionId: RegionId
) {
    val topic: String

    init {
        val topicAnnotation =
            this::class.annotations.find { annotation -> annotation.annotationClass == DomainNotificationTopic::class }
                ?: throw IllegalStateException("DomainNotification not annotated with @DomainNotificationTopic")
        topic = (topicAnnotation as DomainNotificationTopic).value
    }
}