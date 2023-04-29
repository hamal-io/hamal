package io.hamal.backend.infra.module.job_definition.handler

import io.hamal.backend.core.notification.JobDefinitionDomainNotification.Created
import io.hamal.backend.core.port.notification.HandleDomainNotificationPort
import io.hamal.backend.infra.adapter.BackendLoggingFactory

class JobDefinitionCreatedHandler : HandleDomainNotificationPort<Created> {
    companion object {
        val log = BackendLoggingFactory(JobDefinitionCreatedHandler::class)
    }
    override fun handle(notification: Created) {
        log.info("JobDefinition was created $notification")
//        DefaultJobDefinitionStore.jobDefinitions[notification.jobDefinition.id] = notification.jobDefinition
    }
}