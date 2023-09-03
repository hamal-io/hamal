package io.hamal.backend.web.req

import io.hamal.lib.sdk.hub.domain.ApiDefaultSubmittedReq
import io.hamal.lib.sdk.hub.domain.ApiSubmittedReq
import io.hamal.lib.sdk.hub.domain.ApiSubmittedReqWithId
import io.hamal.lib.sdk.hub.domain.ApiSubmittedWithTokenReq
import io.hamal.repository.api.submitted_req.*


internal object Assembler {
    fun assemble(req: SubmittedReq): ApiSubmittedReq {
        return when (val r = req) {

            is SubmittedCreateAccountWithPasswordReq -> ApiSubmittedReqWithId(
                reqId = r.reqId,
                status = r.status,
                id = r.id
            )

            is SubmittedAppendToTopicReq -> ApiSubmittedReqWithId(
                reqId = r.reqId,
                status = r.status,
                id = r.id
            )

            is SubmittedCompleteExecReq -> ApiSubmittedReqWithId(
                reqId = r.reqId,
                status = r.status,
                id = r.id
            )

            is SubmittedCreateFuncReq -> ApiSubmittedReqWithId(
                reqId = r.reqId,
                status = r.status,
                id = r.id
            )

            is SubmittedCreateNamespaceReq -> ApiSubmittedReqWithId(
                reqId = r.reqId,
                status = r.status,
                id = r.id
            )

            is SubmittedCreateTopicReq -> ApiSubmittedReqWithId(
                reqId = r.reqId,
                status = r.status,
                id = r.id
            )

            is SubmittedCreateTriggerReq -> ApiSubmittedReqWithId(
                reqId = r.reqId,
                status = r.status,
                id = r.id
            )

            is SubmittedFailExecReq -> ApiSubmittedReqWithId(
                reqId = r.reqId,
                status = r.status,
                id = r.id
            )

            is SubmittedInvokeExecReq -> ApiSubmittedReqWithId(
                reqId = r.reqId,
                status = r.status,
                id = r.id
            )

            is SubmittedUpdateFuncReq -> ApiSubmittedReqWithId(
                reqId = r.reqId,
                status = r.status,
                id = r.id
            )

            is SubmittedUpdateNamespaceReq -> ApiSubmittedReqWithId(
                reqId = r.reqId,
                status = r.status,
                id = r.id
            )


            is SubmittedSetStateReq -> ApiDefaultSubmittedReq(
                reqId = r.reqId,
                status = r.status
            )

            is SubmittedSignInWithPasswordReq -> ApiSubmittedWithTokenReq(
                reqId = r.reqId,
                status = r.status,
                token = r.token
            )
        }
    }
}