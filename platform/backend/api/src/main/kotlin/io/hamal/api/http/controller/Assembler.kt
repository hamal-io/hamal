package io.hamal.api.http.controller

import io.hamal.lib.domain.request.*
import io.hamal.lib.sdk.api.*
import org.springframework.http.ResponseEntity

fun Requested.accepted(): ResponseEntity<ApiRequested> =
    ResponseEntity.accepted().body(toApiRequested())

// @formatter:off
fun Requested.toApiRequested(): ApiRequested = when (this) {
    is AccountCreateRequested -> ApiTokenRequested(id, status, accountId, listOf(workspaceId), token)
    is AccountCreateAnonymousRequested -> ApiTokenRequested(id, status, accountId, listOf(workspaceId), token)
    is AccountConvertRequested -> ApiAccountConvertRequested(id, status, accountId, token)
    is AuthLoginMetaMaskRequested -> ApiTokenRequested(id, status, accountId, workspaceIds, token)
    is AuthLoginEmailRequested -> ApiTokenRequested(id, status, accountId, workspaceIds,  token)
    is BlueprintCreateRequested -> ApiBlueprintCreateRequested(id, status, blueprintId, workspaceId)
    is BlueprintUpdateRequested -> ApiBlueprintUpdateRequested(id, status, blueprintId)
    is EndpointCreateRequested -> ApiEndpointCreateRequested(id, status, endpointId, workspaceId, funcId)
    is EndpointUpdateRequested -> ApiEndpointUpdateRequested(id, status, endpointId)
    is ExecInvokeRequested -> ApiExecInvokeRequested(id, status, execId, workspaceId, namespaceId)
    is ExtensionCreateRequested -> ApiExtensionCreateRequested(id, status, extensionId, workspaceId)
    is ExtensionUpdateRequested -> ApiExtensionUpdateRequested(id, status, extensionId)
    is FuncCreateRequested -> ApiFuncCreateRequested(id, status, funcId, workspaceId, namespaceId)
    is FuncDeployRequested -> ApiFuncDeployRequested(id, status, funcId)
    is FuncUpdateRequested -> ApiFuncUpdateRequested(id, status, funcId)
    is HookCreateRequested -> ApiHookCreateRequested(id, status, hookId, workspaceId, namespaceId)
    is HookUpdateRequested -> ApiHookUpdateRequested(id, status, hookId)
    is NamespaceCreateRequested -> ApiNamespaceCreateRequested(id, status, namespaceId, workspaceId)
    is NamespaceUpdateRequested -> ApiNamespaceUpdateRequested(id, status, namespaceId)
    is StateSetRequested -> ApiStateSetRequested(id, status)
    is TopicAppendEventRequested -> ApiTopicAppendRequested(id, status, topicId)
    is TopicCreateRequested -> ApiTopicCreateRequested(id, status, topicId, workspaceId, namespaceId, type)
    is TriggerCreateRequested -> ApiTriggerCreateRequested(id, status, triggerId, workspaceId, namespaceId)
    is TriggerStatusRequested -> ApiTriggerStatusRequested(id, status, triggerId, triggerStatus)

    is AuthLogoutRequested,
    is AccountCreateMetaMaskRequested,
    is ExecFailRequested,
    is FeedbackCreateRequested,
    is HookInvokeRequested,
    is TestRequested,
    is ExecCompleteRequested -> throw NotImplementedError()
}
// @formatter:on