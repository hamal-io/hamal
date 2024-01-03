package io.hamal.api.http.controller

import io.hamal.lib.domain.request.*
import io.hamal.lib.domain.vo.GroupDefaultFlowId
import io.hamal.lib.sdk.api.*
import org.springframework.http.ResponseEntity

fun Requested.accepted(): ResponseEntity<ApiRequested> =
    ResponseEntity.accepted().body(toApiSubmitted())

// @formatter:off
fun Requested.toApiSubmitted(): ApiRequested = when (this) {
    is AccountCreateRequested -> ApiTokenRequested(id, status, accountId, listOf(groupId), listOf(GroupDefaultFlowId(groupId , flowId)), token)
    is AccountCreateAnonymousRequested -> ApiTokenRequested(id, status, accountId, listOf(groupId), listOf(GroupDefaultFlowId(groupId , flowId)), token)
    is AccountConvertRequested -> ApiAccountConvertRequested(id, status, accountId, token)
    is AuthLoginMetaMaskRequested -> ApiTokenRequested(id, status, accountId, groupIds, defaultFlowIds, token)
    is AuthLoginEmailRequested -> ApiTokenRequested(id, status, accountId, groupIds, defaultFlowIds, token)
    is BlueprintCreateRequested -> ApiBlueprintCreateRequested(id, status, blueprintId, groupId)
    is BlueprintUpdateRequested -> ApiBlueprintUpdateRequested(id, status, blueprintId)
    is EndpointCreateRequested -> ApiEndpointCreateRequested(id, status, endpointId, groupId, funcId)
    is EndpointUpdateRequested -> ApiEndpointUpdateRequested(id, status, endpointId)
    is ExecInvokeRequested -> ApiExecInvokeRequested(id, status, execId, groupId, flowId)
    is ExtensionCreateRequested -> ApiExtensionCreateRequested(id, status, extensionId, groupId)
    is ExtensionUpdateRequested -> ApiExtensionUpdateRequested(id, status, extensionId)
    is FuncCreateRequested -> ApiFuncCreateRequested(id, status, funcId, groupId, flowId)
    is FuncDeployRequested -> ApiFuncDeployRequested(id, status, funcId)
    is FuncUpdateRequested -> ApiFuncUpdateRequested(id, status, funcId)
    is HookCreateRequested -> ApiHookCreateRequested(id, status, hookId, groupId, flowId)
    is HookUpdateRequested -> ApiHookUpdateRequested(id, status, hookId)
    is FlowCreateRequested -> ApiFlowCreateRequested(id, status, flowId, groupId)
    is FlowUpdateRequested -> ApiFlowUpdateRequested(id, status, flowId)
    is StateSetRequested -> ApiStateSetRequested(id, status)
    is TopicAppendToRequested -> ApiTopicAppendRequested(id, status, topicId)
    is TopicCreateRequested -> ApiTopicCreateRequested(id, status, topicId, groupId, flowId)
    is TriggerCreateRequested -> ApiTriggerCreateSubmitted(id, status, triggerId, groupId, flowId)
    is TriggerStatusRequested -> ApiTriggerStatusSubmitted(id, status, triggerId, triggerStatus)

    is AuthLogoutRequested,
    is AccountCreateMetaMaskRequested,
    is ExecFailRequested,
    is HookInvokeRequested,
    is TestRequested,
    is ExecCompleteRequested -> throw NotImplementedError()
}
// @formatter:on