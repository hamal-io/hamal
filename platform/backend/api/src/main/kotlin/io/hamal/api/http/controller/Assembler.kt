package io.hamal.api.http.controller

import io.hamal.lib.domain.request.*
import io.hamal.lib.domain.vo.GroupDefaultNamespaceId
import io.hamal.lib.sdk.api.*
import org.springframework.http.ResponseEntity

fun Requested.accepted(): ResponseEntity<ApiRequested> =
    ResponseEntity.accepted().body(toApiRequested())

// @formatter:off
fun Requested.toApiRequested(): ApiRequested = when (this) {
    is AccountCreateRequested -> ApiTokenRequested(id, status, accountId, listOf(groupId), listOf(GroupDefaultNamespaceId(groupId , namespaceId)), token)
    is AccountCreateAnonymousRequested -> ApiTokenRequested(id, status, accountId, listOf(groupId), listOf(GroupDefaultNamespaceId(groupId , namespaceId)), token)
    is AccountConvertRequested -> ApiAccountConvertRequested(id, status, accountId, token)
    is AuthLoginMetaMaskRequested -> ApiTokenRequested(id, status, accountId, groupIds, defaultNamespaceIds, token)
    is AuthLoginEmailRequested -> ApiTokenRequested(id, status, accountId, groupIds, defaultNamespaceIds, token)
    is BlueprintCreateRequested -> ApiBlueprintCreateRequested(id, status, blueprintId, groupId)
    is BlueprintUpdateRequested -> ApiBlueprintUpdateRequested(id, status, blueprintId)
    is EndpointCreateRequested -> ApiEndpointCreateRequested(id, status, endpointId, groupId, funcId)
    is EndpointUpdateRequested -> ApiEndpointUpdateRequested(id, status, endpointId)
    is ExecInvokeRequested -> ApiExecInvokeRequested(id, status, execId, groupId, namespaceId)
    is ExtensionCreateRequested -> ApiExtensionCreateRequested(id, status, extensionId, groupId)
    is ExtensionUpdateRequested -> ApiExtensionUpdateRequested(id, status, extensionId)
    is FuncCreateRequested -> ApiFuncCreateRequested(id, status, funcId, groupId, namespaceId)
    is FuncDeployRequested -> ApiFuncDeployRequested(id, status, funcId)
    is FuncUpdateRequested -> ApiFuncUpdateRequested(id, status, funcId)
    is HookCreateRequested -> ApiHookCreateRequested(id, status, hookId, groupId, namespaceId)
    is HookUpdateRequested -> ApiHookUpdateRequested(id, status, hookId)
    is NamespaceCreateRequested -> ApiNamespaceCreateRequested(id, status, namespaceId, groupId)
    is NamespaceUpdateRequested -> ApiNamespaceUpdateRequested(id, status, namespaceId)
    is StateSetRequested -> ApiStateSetRequested(id, status)
    is TopicAppendEventRequested -> ApiTopicAppendRequested(id, status, topicId)
    is TopicGroupCreateRequested -> ApiTopicGroupCreateRequested(id, status, topicId, groupId)
    is TopicPublicCreateRequested -> ApiTopicPublicCreateRequested(id, status, topicId, groupId)
    is TriggerCreateRequested -> ApiTriggerCreateRequested(id, status, triggerId, groupId, namespaceId)
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