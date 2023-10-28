package io.hamal.api.http.endpoint.req

import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.sdk.api.ApiSubmittedReq
import io.hamal.lib.sdk.api.ApiSubmittedReqImpl
import io.hamal.lib.sdk.api.ApiSubmittedWithTokenReq
import io.hamal.repository.api.submitted_req.*


fun SubmittedReq.toApi() = ApiSubmittedReqImpl(
    reqId = reqId,
    status = status,
    id = TODO(),
    groupId = null,
    namespaceId = null,
)

// FIXME
internal object Assembler {
    fun assemble(req: SubmittedReq): ApiSubmittedReq {
        return when (val r = req) {

            is AccountCreateWithPasswordSubmittedReq -> ApiSubmittedReqImpl(
                reqId = r.reqId,
                status = r.status,
                namespaceId = r.namespaceId,
                groupId = r.groupId,
                id = r.id
            )

            is TopicAppendToSubmittedReq -> ApiSubmittedReqImpl(
                reqId = r.reqId,
                status = r.status,
                namespaceId = NamespaceId(1337),
                groupId = r.groupId,
                id = r.id
            )

            is ExecCompleteSubmittedExecReq -> ApiSubmittedReqImpl(
                reqId = r.reqId,
                status = r.status,
                namespaceId = NamespaceId(1337),
                groupId = GroupId(1),
                id = r.id
            )

            is ExtensionSubmittedReq -> ApiSubmittedReqImpl(
                reqId = r.reqId,
                status = r.status,
                namespaceId = NamespaceId(1337),
                groupId = r.groupId,
                id = r.id
            )

            is ExtensionSubmittedUpdateReq -> ApiSubmittedReqImpl(
                reqId = r.reqId,
                status = r.status,
                namespaceId = NamespaceId(1337),
                groupId = r.groupId,
                id = r.id
            )

            is FuncCreateSubmittedReq -> ApiSubmittedReqImpl(
                reqId = r.reqId,
                status = r.status,
                namespaceId = r.namespaceId,
                groupId = r.groupId,
                id = r.id
            )

            is SubmittedCreateHookReq -> ApiSubmittedReqImpl(
                reqId = r.reqId,
                status = r.status,
                namespaceId = r.namespaceId,
                groupId = r.groupId,
                id = r.id
            )

            is SubmittedUpdateHookReq -> ApiSubmittedReqImpl(
                reqId = r.reqId,
                status = r.status,
                namespaceId = NamespaceId(1337),
                groupId = r.groupId,
                id = r.id
            )

            is NamespaceCreateSubmittedReq -> ApiSubmittedReqImpl(
                reqId = r.reqId,
                status = r.status,
                namespaceId = NamespaceId(1337),
                groupId = r.groupId,
                id = r.id
            )

            is TopicCreateSubmittedReq -> ApiSubmittedReqImpl(
                reqId = r.reqId,
                status = r.status,
                namespaceId = r.namespaceId,
                groupId = r.groupId,
                id = r.id
            )

            is SnippetCreateSubmittedReq -> ApiSubmittedReqImpl(
                reqId = r.reqId,
                status = r.status,
                namespaceId = NamespaceId(1337),
                groupId = r.groupId,
                id = r.id
            )

            is SnippetUpdateSubmittedReq -> ApiSubmittedReqImpl(
                reqId = r.reqId,
                status = r.status,
                namespaceId = NamespaceId(1337),
                groupId = r.groupId,
                id = r.id
            )

            is SubmittedCreateTriggerReq -> ApiSubmittedReqImpl(
                reqId = r.reqId,
                status = r.status,
                namespaceId = r.namespaceId,
                groupId = r.groupId,
                id = r.id
            )

            is ExecFailSubmittedExecReq -> ApiSubmittedReqImpl(
                reqId = r.reqId,
                status = r.status,
                namespaceId = NamespaceId(1337),
                groupId = GroupId(1),
                id = r.id
            )

            is ExecInvokeSubmittedReq -> ApiSubmittedReqImpl(
                reqId = r.reqId,
                status = r.status,
                namespaceId = r.namespaceId,
                groupId = r.groupId,
                id = r.id
            )

            is FuncUpdateSubmittedReq -> ApiSubmittedReqImpl(
                reqId = r.reqId,
                status = r.status,
                namespaceId = NamespaceId(1337),
                groupId = r.groupId,
                id = r.id
            )

            is NamespaceUpdateSubmittedReq -> ApiSubmittedReqImpl(
                reqId = r.reqId,
                status = r.status,
                namespaceId = NamespaceId(1337),
                groupId = r.groupId,
                id = r.id
            )


            is StateSetSubmittedReq -> ApiSubmittedReqImpl(
                reqId = r.reqId,
                status = r.status,
                namespaceId = NamespaceId(1337),
                groupId = r.groupId,
                id = r.state.correlation.funcId
            )

            is AuthSignInWithPasswordSubmittedReq -> ApiSubmittedWithTokenReq(
                reqId = r.reqId,
                status = r.status,
                token = r.token
            )

            is SubmittedInvokeHookReq -> ApiSubmittedReqImpl(
                reqId = r.reqId,
                status = r.status,
                namespaceId = NamespaceId(1337),
                groupId = r.groupId,
                id = r.id
            )

            is TestSubmittedReq -> TODO()
        }
    }
}