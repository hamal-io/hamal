package io.hamal.api.http.controller

import io.hamal.lib.domain.request.*
import io.hamal.lib.sdk.api.*
import org.springframework.http.ResponseEntity

fun Requested.accepted(): ResponseEntity<ApiRequested> =
    ResponseEntity.accepted().body(toApiRequested())

// @formatter:off
fun Requested.toApiRequested(): ApiRequested = when (this) {
    is AccountCreateRequested -> ApiTokenRequested(requestId, requestStatus, accountId, listOf(workspaceId), token)
    is AccountCreateAnonymousRequested -> ApiTokenRequested(requestId, requestStatus, id, listOf(workspaceId), token)
    is AccountConvertRequested -> ApiAccountConvertRequested(requestId, requestStatus, id, token)
    is AuthLoginMetaMaskRequested -> ApiTokenRequested(requestId, requestStatus, accountId, workspaceIds, token)
    is AuthLoginEmailRequested -> ApiTokenRequested(requestId, requestStatus, accountId, workspaceIds,  token)
    is BlueprintCreateRequested -> ApiBlueprintCreateRequested(requestId, requestStatus, id)
    is BlueprintUpdateRequested -> ApiBlueprintUpdateRequested(requestId, requestStatus, id)
    is EndpointCreateRequested -> ApiEndpointCreateRequested(requestId, requestStatus, id, workspaceId, funcId)
    is EndpointUpdateRequested -> ApiEndpointUpdateRequested(requestId, requestStatus, id)
    is ExecInvokeRequested -> ApiExecInvokeRequested(requestId, requestStatus, id, workspaceId, namespaceId)
    is ExtensionCreateRequested -> ApiExtensionCreateRequested(requestId, requestStatus, id, workspaceId)
    is ExtensionUpdateRequested -> ApiExtensionUpdateRequested(requestId, requestStatus, id)
    is FeedbackCreateRequested -> ApiFeedbackCreateRequested(requestId, requestStatus, id)
    is FuncCreateRequested -> ApiFuncCreateRequested(requestId, requestStatus, id, workspaceId, namespaceId)
    is FuncDeployRequested -> ApiFuncDeployRequested(requestId, requestStatus, id)
    is FuncUpdateRequested -> ApiFuncUpdateRequested(requestId, requestStatus, id)
    is HookCreateRequested -> ApiHookCreateRequested(requestId, requestStatus, id, workspaceId, namespaceId)
    is HookUpdateRequested -> ApiHookUpdateRequested(requestId, requestStatus, id)
    is NamespaceAppendRequested -> ApiNamespaceAppendRequested(requestId, requestStatus,  id, workspaceId)
    is NamespaceUpdateRequested -> ApiNamespaceUpdateRequested(requestId, requestStatus, id)
    is StateSetRequested -> ApiStateSetRequested(requestId, requestStatus)
    is TopicAppendEventRequested -> ApiTopicAppendRequested(requestId, requestStatus, id)
    is TopicCreateRequested -> ApiTopicCreateRequested(requestId, requestStatus, id, workspaceId, namespaceId, type)
    is TriggerCreateRequested -> ApiTriggerCreateRequested(requestId, requestStatus, id, workspaceId, namespaceId)
    is TriggerStatusRequested -> ApiTriggerStatusRequested(requestId, requestStatus, id, status)

    is AuthLogoutRequested,
    is AccountCreateMetaMaskRequested,
    is ExecFailRequested,
    is HookInvokeRequested,
    is TestRequested,
    is ExecCompleteRequested -> throw NotImplementedError()
}
// @formatter:on