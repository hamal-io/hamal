package io.hamal.api.http.controller

import io.hamal.lib.sdk.api.*
import io.hamal.repository.api.submitted_req.*
import org.springframework.http.ResponseEntity

fun Submitted.accepted(): ResponseEntity<ApiSubmitted> =
    ResponseEntity.accepted().body(toApiSubmitted())

fun Submitted.toApiSubmitted(): ApiSubmitted = when (this) {
    is AccountCreateSubmitted -> ApiTokenSubmitted(id, status, accountId, listOf(groupId), token, name)
    is AccountConvertSubmitted -> ApiConvertAccountSubmitted(id, status, accountId, token, name)
    is AuthLoginSubmitted -> ApiTokenSubmitted(id, status, accountId, groupIds, token, name)
    is BlueprintCreateSubmitted -> ApiBlueprintCreateSubmitted(id, status, blueprintId, groupId)
    is BlueprintUpdateSubmitted -> ApiBlueprintUpdateSubmitted(id, status, blueprintId)
    is ExecInvokeSubmitted -> ApiExecInvokeSubmitted(id, status, execId, groupId, namespaceId)
    is ExtensionCreateSubmitted -> ApiExtensionCreateSubmitted(id, status, extensionId, groupId)
    is ExtensionUpdateSubmitted -> ApiExtensionUpdateSubmitted(id, status, extensionId)
    is FuncCreateSubmitted -> ApiFuncCreateSubmitted(id, status, funcId, groupId, namespaceId)
    is FuncDeploySubmitted -> ApiFuncDeploySubmitted(id, status, funcId, versionToDeploy)
    is FuncDeployLatestSubmitted -> ApiFuncDeployLatestSubmitted(id, status, funcId)
    is FuncUpdateSubmitted -> ApiFuncUpdateSubmitted(id, status, funcId)
    is HookCreateSubmitted -> ApiHookCreateSubmitted(id, status, hookId, groupId, namespaceId)
    is HookUpdateSubmitted -> ApiHookUpdateSubmitted(id, status, hookId)
    is NamespaceCreateSubmitted -> ApiNamespaceCreateSubmitted(id, status, namespaceId, groupId)
    is NamespaceUpdateSubmitted -> ApiNamespaceUpdateSubmitted(id, status, namespaceId)
    is StateSetSubmitted -> ApiStateSetSubmitted(id, status)
    is TopicAppendToSubmitted -> ApiTopicAppendSubmitted(id, status, topicId)
    is TopicCreateSubmitted -> ApiTopicCreateSubmitted(id, status, topicId, groupId, namespaceId)
    is TriggerCreateSubmitted -> ApiTriggerCreateSubmitted(id, status, triggerId, groupId, namespaceId)

    is TestSubmitted,
    is ExecFailSubmitted,
    is HookInvokeSubmitted,
    is ExecCompleteSubmitted -> throw NotImplementedError()

}
