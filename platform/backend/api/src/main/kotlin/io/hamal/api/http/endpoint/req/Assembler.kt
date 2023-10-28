package io.hamal.api.http.endpoint.req

import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.sdk.api.ApiDefaultSubmittedReq
import io.hamal.lib.sdk.api.ApiSubmittedReq
import io.hamal.lib.sdk.api.ApiSubmittedReqWithId
import io.hamal.lib.sdk.api.ApiSubmittedWithTokenReq
import io.hamal.repository.api.submitted_req.*

// FIXME
internal object Assembler {
    fun assemble(req: SubmittedReq): ApiSubmittedReq {
        return when (val r = req) {

            is AccountCreateWithPasswordSubmittedReq -> ApiSubmittedReqWithId(
                reqId = r.reqId,
                status = r.status,
                namespaceId = r.namespaceId,
                groupId = r.groupId,
                id = r.id
            )

            is TopicAppendToSubmittedReq -> ApiSubmittedReqWithId(
                reqId = r.reqId,
                status = r.status,
                namespaceId = NamespaceId(1337),
                groupId = r.groupId,
                id = r.id
            )

            is ExecCompleteSubmittedExecReq -> ApiSubmittedReqWithId(
                reqId = r.reqId,
                status = r.status,
                namespaceId = NamespaceId(1337),
                groupId = GroupId(1),
                id = r.id
            )

            is ExtensionSubmittedReq -> ApiSubmittedReqWithId(
                reqId = r.reqId,
                status = r.status,
                namespaceId = NamespaceId(1337),
                groupId = r.groupId,
                id = r.id
            )

            is ExtensionSubmittedUpdateReq -> ApiSubmittedReqWithId(
                reqId = r.reqId,
                status = r.status,
                namespaceId = NamespaceId(1337),
                groupId = r.groupId,
                id = r.id
            )

            is FuncCreateSubmittedReq -> ApiSubmittedReqWithId(
                reqId = r.reqId,
                status = r.status,
                namespaceId = r.namespaceId ?: NamespaceId(1337),
                groupId = r.groupId,
                id = r.id
            )

            is SubmittedCreateHookReq -> ApiSubmittedReqWithId(
                reqId = r.reqId,
                status = r.status,
                namespaceId = r.namespaceId ?: NamespaceId(1337),
                groupId = r.groupId,
                id = r.id
            )

            is SubmittedUpdateHookReq -> ApiSubmittedReqWithId(
                reqId = r.reqId,
                status = r.status,
                namespaceId = NamespaceId(1337),
                groupId = r.groupId,
                id = r.id
            )

            is NamespaceCreateSubmittedReq -> ApiSubmittedReqWithId(
                reqId = r.reqId,
                status = r.status,
                namespaceId = NamespaceId(1337),
                groupId = r.groupId,
                id = r.id
            )

            is TopicCreateSubmittedReq -> ApiSubmittedReqWithId(
                reqId = r.reqId,
                status = r.status,
                namespaceId = r.namespaceId,
                groupId = r.groupId,
                id = r.id
            )

            is SnippetCreateSubmittedReq -> ApiSubmittedReqWithId(
                reqId = r.reqId,
                status = r.status,
                namespaceId = NamespaceId(1337),
                groupId = r.groupId,
                id = r.id
            )

            is SnippetUpdateSubmittedReq -> ApiSubmittedReqWithId(
                reqId = r.reqId,
                status = r.status,
                namespaceId = NamespaceId(1337),
                groupId = r.groupId,
                id = r.id
            )

            is SubmittedCreateTriggerReq -> ApiSubmittedReqWithId(
                reqId = r.reqId,
                status = r.status,
                namespaceId = r.namespaceId ?: NamespaceId(1337),
                groupId = r.groupId,
                id = r.id
            )

            is ExecFailSubmittedExecReq -> ApiSubmittedReqWithId(
                reqId = r.reqId,
                status = r.status,
                namespaceId = NamespaceId(1337),
                groupId = GroupId(1),
                id = r.id
            )

            is ExecInvokeSubmittedReq -> ApiSubmittedReqWithId(
                reqId = r.reqId,
                status = r.status,
                namespaceId = r.namespaceId,
                groupId = r.groupId,
                id = r.id
            )

            is FuncUpdateSubmittedReq -> ApiSubmittedReqWithId(
                reqId = r.reqId,
                status = r.status,
                namespaceId = NamespaceId(1337),
                groupId = r.groupId,
                id = r.id
            )

            is NamespaceUpdateSubmittedReq -> ApiSubmittedReqWithId(
                reqId = r.reqId,
                status = r.status,
                namespaceId = NamespaceId(1337),
                groupId = r.groupId,
                id = r.id
            )


            is StateSetSubmittedReq -> ApiDefaultSubmittedReq(
                reqId = r.reqId,
                status = r.status,
                namespaceId = NamespaceId(1337),
                groupId = r.groupId
            )

            is AuthSignInWithPasswordSubmittedReq -> ApiSubmittedWithTokenReq(
                reqId = r.reqId,
                status = r.status,
                token = r.token
            )

            is SubmittedInvokeHookReq -> ApiSubmittedReqWithId(
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