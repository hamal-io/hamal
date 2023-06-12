package io.hamal.lib.sdk.service

import io.hamal.lib.domain.req.InvokeAdhocReq
import io.hamal.lib.domain.req.SubmittedInvokeAdhocReq
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body

interface AdhocService {
    fun submit(req: InvokeAdhocReq): SubmittedInvokeAdhocReq
}

data class DefaultAdhocService(val template: HttpTemplate) : AdhocService {
    override fun submit(req: InvokeAdhocReq): SubmittedInvokeAdhocReq {
        println(req)
        return template
            .post("/v1/adhoc")
            .body(req)
            .execute(SubmittedInvokeAdhocReq::class)
    }

}