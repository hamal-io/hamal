package io.hamal.api.http.endpoint

import io.hamal.lib.sdk.api.*
import io.hamal.repository.api.submitted_req.*
import org.springframework.http.ResponseEntity

fun Submitted.accepted(): ResponseEntity<ApiSubmitted> =
    ResponseEntity.accepted().body(toApiSubmitted())

fun Submitted.toApiSubmitted(): ApiSubmitted = when (this) {
    is AccountCreateSubmitted -> ApiTokenSubmitted(reqId, status, id, listOf(groupId), token)
    is AuthLoginSubmitted -> ApiTokenSubmitted(reqId, status, accountId, groupIds, token)
    is ExecInvokeSubmitted -> ApiExecInvokeSubmitted(reqId, status, id, groupId, namespaceId)
    is ExtensionCreateSubmitted -> ApiExtensionCreateSubmitted(reqId, status, id, groupId)
    is ExtensionUpdateSubmitted -> ApiExtensionUpdateSubmitted(reqId, status, id)
    is FuncCreateSubmitted -> ApiFuncCreateSubmitted(reqId, status, id, groupId, namespaceId)
    is FuncUpdateSubmitted -> ApiFuncUpdateSubmitted(reqId, status, id)
    is HookCreateSubmitted -> ApiHookCreateSubmitted(reqId, status, id, groupId, namespaceId)
    is HookUpdateSubmitted -> ApiHookUpdateSubmitted(reqId, status, id)
    is NamespaceCreateSubmitted -> ApiNamespaceCreateSubmitted(reqId, status, id, groupId)
    is NamespaceUpdateSubmitted -> ApiNamespaceUpdateSubmitted(reqId, status, id)
    is SnippetCreateSubmitted -> ApiSnippetCreateSubmitted(reqId, status, id, groupId)
    is SnippetUpdateSubmitted -> ApiSnippetUpdateSubmitted(reqId, status, id)
    is StateSetSubmitted -> ApiStateSetSubmitted(reqId, status)
    is TopicAppendToSubmitted -> ApiTopicAppendSubmitted(reqId, status, id)
    is TopicCreateSubmitted -> ApiTopicCreateSubmitted(reqId, status, id, groupId, namespaceId)
    is TriggerCreateSubmitted -> ApiTriggerCreateSubmitted(reqId, status, id, groupId, namespaceId)

    is TestSubmitted,
    is ExecFailSubmitted,
    is HookInvokeSubmitted,
    is ExecCompleteSubmitted -> throw NotImplementedError()

}
