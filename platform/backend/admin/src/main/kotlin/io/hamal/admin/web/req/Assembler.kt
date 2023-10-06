package io.hamal.admin.web.req

import io.hamal.lib.sdk.admin.AdminDefaultSubmittedReq
import io.hamal.lib.sdk.admin.AdminSubmittedReq
import io.hamal.lib.sdk.admin.AdminSubmittedReqWithId
import io.hamal.lib.sdk.admin.AdminSubmittedWithTokenReq
import io.hamal.repository.api.submitted_req.*


internal object Assembler {
    fun assemble(req: SubmittedReq): AdminSubmittedReq {
        return when (req) {

            is SubmittedCreateAccountWithPasswordReq -> AdminSubmittedReqWithId(
                reqId = req.reqId,
                status = req.status,
                groupId = req.groupId,
                id = req.id
            )

            is SubmittedAppendToTopicReq -> AdminSubmittedReqWithId(
                reqId = req.reqId,
                status = req.status,
                groupId = req.groupId,
                id = req.id
            )

            is SubmittedCompleteExecReq -> AdminSubmittedReqWithId(
                reqId = req.reqId,
                status = req.status,
                groupId = req.groupId,
                id = req.id
            )

            is SubmittedCreateFuncReq -> AdminSubmittedReqWithId(
                reqId = req.reqId,
                status = req.status,
                groupId = req.groupId,
                id = req.id
            )

            is SubmittedCreateNamespaceReq -> AdminSubmittedReqWithId(
                reqId = req.reqId,
                status = req.status,
                groupId = req.groupId,
                id = req.id
            )

            is SubmittedCreateTopicReq -> AdminSubmittedReqWithId(
                reqId = req.reqId,
                status = req.status,
                groupId = req.groupId,
                id = req.id
            )

            is SubmittedCreateTriggerReq -> AdminSubmittedReqWithId(
                reqId = req.reqId,
                status = req.status,
                groupId = req.groupId,
                id = req.id
            )

            is SubmittedFailExecReq -> AdminSubmittedReqWithId(
                reqId = req.reqId,
                status = req.status,
                groupId = req.groupId,
                id = req.id
            )

            is SubmittedInvokeExecReq -> AdminSubmittedReqWithId(
                reqId = req.reqId,
                status = req.status,
                groupId = req.groupId,
                id = req.id
            )

            is SubmittedUpdateFuncReq -> AdminSubmittedReqWithId(
                reqId = req.reqId,
                status = req.status,
                groupId = req.groupId,
                id = req.id
            )

            is SubmittedUpdateNamespaceReq -> AdminSubmittedReqWithId(
                reqId = req.reqId,
                status = req.status,
                groupId = req.groupId,
                id = req.id
            )


            is SubmittedSetStateReq -> AdminDefaultSubmittedReq(
                reqId = req.reqId,
                status = req.status,
                groupId = req.groupId
            )

            is SubmittedSignInWithPasswordReq -> AdminSubmittedWithTokenReq(
                reqId = req.reqId,
                status = req.status,
                token = req.token
            )

            is TestSubmittedReq -> TODO()
        }
    }
}