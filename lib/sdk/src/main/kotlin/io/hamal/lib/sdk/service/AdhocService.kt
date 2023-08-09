package io.hamal.lib.sdk.service

import io.hamal.lib.domain.req.InvokeAdhocReq
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.sdk.domain.ApiSubmittedReqWithDomainId

interface AdhocService {
    fun submit(req: InvokeAdhocReq): ApiSubmittedReqWithDomainId
}

data class DefaultAdhocService(val template: HttpTemplate) : AdhocService {
    override fun submit(req: InvokeAdhocReq): ApiSubmittedReqWithDomainId {
        return template
            .post("/v1/adhoc")
            .body(req)
            .execute(ApiSubmittedReqWithDomainId::class)
    }
}