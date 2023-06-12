package io.hamal.lib.sdk.service

import io.hamal.lib.domain.req.AdhocInvocationReq
import io.hamal.lib.domain.req.SubmittedAdhocInvocationReq
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body

interface AdhocService {
    fun submit(req: AdhocInvocationReq): SubmittedAdhocInvocationReq
}

class DefaultAdhocService : AdhocService {
    override fun submit(req: AdhocInvocationReq): SubmittedAdhocInvocationReq {
        return HttpTemplate("http://localhost:8084")
            .post("/v1/adhoc")
            .body(req)
            .execute(SubmittedAdhocInvocationReq::class)
    }

}