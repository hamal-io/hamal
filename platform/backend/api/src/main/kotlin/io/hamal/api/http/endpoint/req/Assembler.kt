package io.hamal.api.http.endpoint.req

import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.sdk.api.ApiSubmittedReq
import io.hamal.lib.sdk.api.ApiSubmittedReqImpl
import io.hamal.lib.sdk.api.ApiSubmittedWithTokenReq
import io.hamal.repository.api.submitted_req.*


fun Submitted.toApi() = ApiSubmittedReqImpl(
    reqId = reqId,
    status = status,
    id = TODO(),
    groupId = null,
    namespaceId = null,
)

// FIXME
internal object Assembler {
    fun assemble(req: Submitted): ApiSubmittedReq {
        return when (val r = req) {

            is AccountCreateWithPasswordSubmitted -> ApiSubmittedReqImpl(
                reqId = r.reqId,
                status = r.status,
                namespaceId = r.namespaceId,
                groupId = r.groupId,
                id = r.id
            )

            is TopicAppendToSubmitted -> ApiSubmittedReqImpl(
                reqId = r.reqId,
                status = r.status,
                namespaceId = NamespaceId(1337),
                groupId = r.groupId,
                id = r.id
            )

            is ExecCompleteSubmitted -> ApiSubmittedReqImpl(
                reqId = r.reqId,
                status = r.status,
                namespaceId = NamespaceId(1337),
                groupId = GroupId(1),
                id = r.id
            )

            is ExtensionCreateSubmitted -> ApiSubmittedReqImpl(
                reqId = r.reqId,
                status = r.status,
                namespaceId = NamespaceId(1337),
                groupId = r.groupId,
                id = r.id
            )

            is ExtensionUpdateSubmitted -> ApiSubmittedReqImpl(
                reqId = r.reqId,
                status = r.status,
                namespaceId = NamespaceId(1337),
                groupId = r.groupId,
                id = r.id
            )

            is FuncCreateSubmitted -> ApiSubmittedReqImpl(
                reqId = r.reqId,
                status = r.status,
                namespaceId = r.namespaceId,
                groupId = r.groupId,
                id = r.id
            )

            is HookCreateSubmitted -> ApiSubmittedReqImpl(
                reqId = r.reqId,
                status = r.status,
                namespaceId = r.namespaceId,
                groupId = r.groupId,
                id = r.id
            )

            is HookUpdateSubmitted -> ApiSubmittedReqImpl(
                reqId = r.reqId,
                status = r.status,
                namespaceId = NamespaceId(1337),
                groupId = r.groupId,
                id = r.id
            )

            is NamespaceCreateSubmitted -> ApiSubmittedReqImpl(
                reqId = r.reqId,
                status = r.status,
                namespaceId = NamespaceId(1337),
                groupId = r.groupId,
                id = r.id
            )

            is TopicCreateSubmitted -> ApiSubmittedReqImpl(
                reqId = r.reqId,
                status = r.status,
                namespaceId = r.namespaceId,
                groupId = r.groupId,
                id = r.id
            )

            is SnippetCreateSubmitted -> ApiSubmittedReqImpl(
                reqId = r.reqId,
                status = r.status,
                namespaceId = NamespaceId(1337),
                groupId = r.groupId,
                id = r.id
            )

            is SnippetUpdateSubmitted -> ApiSubmittedReqImpl(
                reqId = r.reqId,
                status = r.status,
                namespaceId = NamespaceId(1337),
                groupId = r.groupId,
                id = r.id
            )

            is TriggerCreateSubmitted -> ApiSubmittedReqImpl(
                reqId = r.reqId,
                status = r.status,
                namespaceId = r.namespaceId,
                groupId = r.groupId,
                id = r.id
            )

            is ExecFailSubmittedExec -> ApiSubmittedReqImpl(
                reqId = r.reqId,
                status = r.status,
                namespaceId = NamespaceId(1337),
                groupId = GroupId(1),
                id = r.id
            )

            is ExecInvokeSubmitted -> ApiSubmittedReqImpl(
                reqId = r.reqId,
                status = r.status,
                namespaceId = r.namespaceId,
                groupId = r.groupId,
                id = r.id
            )

            is FuncUpdateSubmitted -> ApiSubmittedReqImpl(
                reqId = r.reqId,
                status = r.status,
                namespaceId = NamespaceId(1337),
                groupId = r.groupId,
                id = r.id
            )

            is NamespaceUpdateSubmitted -> ApiSubmittedReqImpl(
                reqId = r.reqId,
                status = r.status,
                namespaceId = NamespaceId(1337),
                groupId = r.groupId,
                id = r.id
            )


            is StateSetSubmitted -> ApiSubmittedReqImpl(
                reqId = r.reqId,
                status = r.status,
                namespaceId = NamespaceId(1337),
                groupId = r.groupId,
                id = r.state.correlation.funcId
            )

            is AuthSignInWithPasswordSubmitted -> ApiSubmittedWithTokenReq(
                reqId = r.reqId,
                status = r.status,
                token = r.token
            )

            is HookInvokeSubmitted -> ApiSubmittedReqImpl(
                reqId = r.reqId,
                status = r.status,
                namespaceId = NamespaceId(1337),
                groupId = r.groupId,
                id = r.id
            )

            is TestSubmitted -> TODO()
        }
    }
}