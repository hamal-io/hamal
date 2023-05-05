package io.hamal.backend.infra.module.job_definition.handler

import io.hamal.backend.core.job_definition.JobDefinitionCreatedNotification
import io.hamal.backend.core.logger
import io.hamal.lib.ddd.port.HandleDomainNotificationPort

class JobDefinitionCreatedHandler : HandleDomainNotificationPort<JobDefinitionCreatedNotification> {
    private val log = logger(JobDefinitionCreatedHandler::class)

    override fun handle(notification: JobDefinitionCreatedNotification) {
        log.info("JobDefinition was created $notification")
//        DefaultJobDefinitionStore.jobDefinitions[notification.jobDefinition.id] = notification.jobDefinition
    }
}