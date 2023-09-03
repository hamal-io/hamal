package io.hamal.lib.sdk.hub.service

import io.hamal.lib.domain.req.InvokeAdhocReq
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.sdk.hub.domain.ApiSubmittedReqWithId

interface AdhocService {
    fun submit(req: InvokeAdhocReq): ApiSubmittedReqWithId
}

internal class DefaultAdhocService(
    private val template: HttpTemplate
) : AdhocService {
    override fun submit(req: InvokeAdhocReq): ApiSubmittedReqWithId {
        return template
            .post("/v1/adhoc")
            .body(req)
            .execute(ApiSubmittedReqWithId::class)
    }
}