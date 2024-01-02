package io.hamal.api.http.controller

import io.hamal.lib.domain.submitted.*
import io.hamal.lib.domain.vo.GroupDefaultFlowId
import io.hamal.lib.sdk.api.*
import org.springframework.http.ResponseEntity

fun Submitted.accepted(): ResponseEntity<ApiSubmitted> =
    ResponseEntity.accepted().body(toApiSubmitted())

// @formatter:off
fun Submitted.toApiSubmitted(): ApiSubmitted = when (this) {
    is AccountCreateSubmitted -> ApiTokenSubmitted(id, status, accountId, listOf(groupId), listOf(GroupDefaultFlowId(groupId , flowId)), token)
    is AccountCreateAnonymousSubmitted -> ApiTokenSubmitted(id, status, accountId, listOf(groupId), listOf(GroupDefaultFlowId(groupId , flowId)), token)
    is AccountConvertSubmitted -> ApiAccountConvertSubmitted(id, status, accountId, token)
    is AuthLoginMetaMaskSubmitted -> ApiTokenSubmitted(id, status, accountId, groupIds, defaultFlowIds, token)
    is AuthLoginEmailSubmitted -> ApiTokenSubmitted(id, status, accountId, groupIds, defaultFlowIds, token)
    is BlueprintCreateSubmitted -> ApiBlueprintCreateSubmitted(id, status, blueprintId, groupId)
    is BlueprintUpdateSubmitted -> ApiBlueprintUpdateSubmitted(id, status, blueprintId)
    is EndpointCreateSubmitted -> ApiEndpointCreateSubmitted(id, status, endpointId, groupId, funcId)
    is EndpointUpdateSubmitted -> ApiEndpointUpdateSubmitted(id, status, endpointId)
    is ExecInvokeSubmitted -> ApiExecInvokeSubmitted(id, status, execId, groupId, flowId)
    is ExtensionCreateSubmitted -> ApiExtensionCreateSubmitted(id, status, extensionId, groupId)
    is ExtensionUpdateSubmitted -> ApiExtensionUpdateSubmitted(id, status, extensionId)
    is FuncCreateSubmitted -> ApiFuncCreateSubmitted(id, status, funcId, groupId, flowId)
    is FuncDeploySubmitted -> ApiFuncDeploySubmitted(id, status, funcId)
    is FuncUpdateSubmitted -> ApiFuncUpdateSubmitted(id, status, funcId)
    is HookCreateSubmitted -> ApiHookCreateSubmitted(id, status, hookId, groupId, flowId)
    is HookUpdateSubmitted -> ApiHookUpdateSubmitted(id, status, hookId)
    is FlowCreateSubmitted -> ApiFlowCreateSubmitted(id, status, flowId, groupId)
    is FlowUpdateSubmitted -> ApiFlowUpdateSubmitted(id, status, flowId)
    is StateSetSubmitted -> ApiStateSetSubmitted(id, status)
    is TopicAppendToSubmitted -> ApiTopicAppendSubmitted(id, status, topicId)
    is TopicCreateSubmitted -> ApiTopicCreateSubmitted(id, status, topicId, groupId, flowId)
    is TriggerCreateSubmitted -> ApiTriggerCreateSubmitted(id, status, triggerId, groupId, flowId)
    is TriggerStatusSubmitted -> ApiTriggerStatusSubmitted(id, status, triggerId, triggerStatus)

    is AuthLogoutSubmitted,
    is AccountCreateMetaMaskSubmitted,
    is TestSubmitted,
    is ExecFailSubmitted,
    is HookInvokeSubmitted,
    is ExecCompleteSubmitted -> throw NotImplementedError()
}
// @formatter:on