package io.hamal.admin.web.req

import io.hamal.lib.sdk.admin.AdminDefaultSubmittedReq
import io.hamal.lib.sdk.admin.AdminSubmittedReq
import io.hamal.lib.sdk.admin.AdminSubmittedReqWithId
import io.hamal.lib.sdk.admin.AdminSubmittedWithTokenReq
import io.hamal.repository.api.submitted_req.*


internal object Assembler {
    fun assemble(req: SubmittedReq): AdminSubmittedReq {
        return when (val r = req) {

            is SubmittedCreateAccountWithPasswordReq -> AdminSubmittedReqWithId(
                reqId = r.reqId,
                status = r.status,
                id = r.id
            )

            is SubmittedAppendToTopicReq -> AdminSubmittedReqWithId(
                reqId = r.reqId,
                status = r.status,
                id = r.id
            )

            is SubmittedCompleteExecReq -> AdminSubmittedReqWithId(
                reqId = r.reqId,
                status = r.status,
                id = r.id
            )

            is SubmittedCreateFuncReq -> AdminSubmittedReqWithId(
                reqId = r.reqId,
                status = r.status,
                id = r.id
            )

            is SubmittedCreateNamespaceReq -> AdminSubmittedReqWithId(
                reqId = r.reqId,
                status = r.status,
                id = r.id
            )

            is SubmittedCreateTopicReq -> AdminSubmittedReqWithId(
                reqId = r.reqId,
                status = r.status,
                id = r.id
            )

            is SubmittedCreateTriggerReq -> AdminSubmittedReqWithId(
                reqId = r.reqId,
                status = r.status,
                id = r.id
            )

            is SubmittedFailExecReq -> AdminSubmittedReqWithId(
                reqId = r.reqId,
                status = r.status,
                id = r.id
            )

            is SubmittedInvokeExecReq -> AdminSubmittedReqWithId(
                reqId = r.reqId,
                status = r.status,
                id = r.id
            )

            is SubmittedUpdateFuncReq -> AdminSubmittedReqWithId(
                reqId = r.reqId,
                status = r.status,
                id = r.id
            )

            is SubmittedUpdateNamespaceReq -> AdminSubmittedReqWithId(
                reqId = r.reqId,
                status = r.status,
                id = r.id
            )


            is SubmittedSetStateReq -> AdminDefaultSubmittedReq(
                reqId = r.reqId,
                status = r.status
            )

            is SubmittedSignInWithPasswordReq -> AdminSubmittedWithTokenReq(
                reqId = r.reqId,
                status = r.status,
                token = r.token
            )
        }
    }
}