package io.hamal.backend.infra.module.flow_definition.handler

import io.hamal.backend.core.notification.FlowDefinitionDomainNotification.Created
import io.hamal.backend.core.port.notification.HandleDomainNotificationPort
import io.hamal.backend.infra.adapter.BackendLoggingFactory

class FlowDefinitionCreatedHandler : HandleDomainNotificationPort<Created> {
    companion object {
        val log = BackendLoggingFactory(FlowDefinitionCreatedHandler::class)
    }
    override fun handle(notification: Created) {
        log.info("FlowDefinition was created $notification")
//        DefaultFlowDefinitionStore.flowDefinitions[notification.flowDefinition.id] = notification.flowDefinition
    }
}