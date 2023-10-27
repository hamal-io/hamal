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

            is SubmittedCreateAccountWithPasswordReq -> ApiSubmittedReqWithId(
                reqId = r.reqId,
                status = r.status,
                namespaceId = r.namespaceId,
                groupId = r.groupId,
                id = r.id
            )

            is SubmittedAppendToTopicReq -> ApiSubmittedReqWithId(
                reqId = r.reqId,
                status = r.status,
                namespaceId = NamespaceId(1337),
                groupId = r.groupId,
                id = r.id
            )

            is SubmittedCompleteExecReq -> ApiSubmittedReqWithId(
                reqId = r.reqId,
                status = r.status,
                namespaceId = NamespaceId(1337),
                groupId = GroupId(1),
                id = r.id
            )

            is SubmittedCreateExtensionReq -> ApiSubmittedReqWithId(
                reqId = r.reqId,
                status = r.status,
                namespaceId = NamespaceId(1337),
                groupId = r.groupId,
                id = r.id
            )

            is SubmittedUpdateExtensionReq -> ApiSubmittedReqWithId(
                reqId = r.reqId,
                status = r.status,
                namespaceId = NamespaceId(1337),
                groupId = r.groupId,
                id = r.id
            )

            is SubmittedCreateFuncReq -> ApiSubmittedReqWithId(
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
                namespaceId = r.namespaceId ?: NamespaceId(1337),
                groupId = r.groupId,
                id = r.id
            )

            is SubmittedCreateNamespaceReq -> ApiSubmittedReqWithId(
                reqId = r.reqId,
                status = r.status,
                namespaceId = NamespaceId(1337),
                groupId = r.groupId,
                id = r.id
            )

            is SubmittedCreateTopicReq -> ApiSubmittedReqWithId(
                reqId = r.reqId,
                status = r.status,
                namespaceId = r.namespaceId,
                groupId = r.groupId,
                id = r.id
            )

            is SubmittedCreateSnippetReq -> ApiSubmittedReqWithId(
                reqId = r.reqId,
                status = r.status,
                namespaceId = NamespaceId(1337),
                groupId = r.groupId,
                id = r.id
            )

            is SubmittedUpdateSnippetReq -> ApiSubmittedReqWithId(
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

            is SubmittedFailExecReq -> ApiSubmittedReqWithId(
                reqId = r.reqId,
                status = r.status,
                namespaceId = NamespaceId(1337),
                groupId = GroupId(1),
                id = r.id
            )

            is SubmittedInvokeExecReq -> ApiSubmittedReqWithId(
                reqId = r.reqId,
                status = r.status,
                namespaceId = r.namespaceId,
                groupId = r.groupId,
                id = r.id
            )

            is SubmittedUpdateFuncReq -> ApiSubmittedReqWithId(
                reqId = r.reqId,
                status = r.status,
                namespaceId = NamespaceId(1337),
                groupId = r.groupId,
                id = r.id
            )

            is SubmittedUpdateNamespaceReq -> ApiSubmittedReqWithId(
                reqId = r.reqId,
                status = r.status,
                namespaceId = NamespaceId(1337),
                groupId = r.groupId,
                id = r.id
            )


            is SubmittedSetStateReq -> ApiDefaultSubmittedReq(
                reqId = r.reqId,
                status = r.status,
                namespaceId = NamespaceId(1337),
                groupId = r.groupId
            )

            is SubmittedSignInWithPasswordReq -> ApiSubmittedWithTokenReq(
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