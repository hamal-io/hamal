package io.hamal.backend.infra.module.job_definition.handler

import io.hamal.backend.core.notification.JobDefinitionDomainNotification.Created
import io.hamal.backend.core.port.logger
import io.hamal.backend.core.port.notification.HandleDomainNotificationPort

class JobDefinitionCreatedHandler : HandleDomainNotificationPort<Created> {
    private val log = logger(JobDefinitionCreatedHandler::class)

    override fun handle(notification: Created) {
        log.info("JobDefinition was created $notification")
//        DefaultJobDefinitionStore.jobDefinitions[notification.jobDefinition.id] = notification.jobDefinition
    }
}