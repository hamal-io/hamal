package io.hamal.backend.web.req

import io.hamal.lib.sdk.hub.HubDefaultSubmittedReq
import io.hamal.lib.sdk.hub.HubSubmittedReq
import io.hamal.lib.sdk.hub.HubSubmittedReqWithId
import io.hamal.lib.sdk.hub.HubSubmittedWithTokenReq
import io.hamal.repository.api.submitted_req.*


internal object Assembler {
    fun assemble(req: SubmittedReq): HubSubmittedReq {
        return when (val r = req) {

            is SubmittedCreateAccountWithPasswordReq -> HubSubmittedReqWithId(
                reqId = r.reqId,
                status = r.status,
                id = r.id
            )

            is SubmittedAppendToTopicReq -> HubSubmittedReqWithId(
                reqId = r.reqId,
                status = r.status,
                id = r.id
            )

            is SubmittedCompleteExecReq -> HubSubmittedReqWithId(
                reqId = r.reqId,
                status = r.status,
                id = r.id
            )

            is SubmittedCreateFuncReq -> HubSubmittedReqWithId(
                reqId = r.reqId,
                status = r.status,
                id = r.id
            )

            is SubmittedCreateNamespaceReq -> HubSubmittedReqWithId(
                reqId = r.reqId,
                status = r.status,
                id = r.id
            )

            is SubmittedCreateTopicReq -> HubSubmittedReqWithId(
                reqId = r.reqId,
                status = r.status,
                id = r.id
            )

            is SubmittedCreateTriggerReq -> HubSubmittedReqWithId(
                reqId = r.reqId,
                status = r.status,
                id = r.id
            )

            is SubmittedFailExecReq -> HubSubmittedReqWithId(
                reqId = r.reqId,
                status = r.status,
                id = r.id
            )

            is SubmittedInvokeExecReq -> HubSubmittedReqWithId(
                reqId = r.reqId,
                status = r.status,
                id = r.id
            )

            is SubmittedUpdateFuncReq -> HubSubmittedReqWithId(
                reqId = r.reqId,
                status = r.status,
                id = r.id
            )

            is SubmittedUpdateNamespaceReq -> HubSubmittedReqWithId(
                reqId = r.reqId,
                status = r.status,
                id = r.id
            )


            is SubmittedSetStateReq -> HubDefaultSubmittedReq(
                reqId = r.reqId,
                status = r.status
            )

            is SubmittedSignInWithPasswordReq -> HubSubmittedWithTokenReq(
                reqId = r.reqId,
                status = r.status,
                token = r.token
            )
        }
    }
}