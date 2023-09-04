package io.hamal.lib.sdk.hub

import io.hamal.lib.domain.req.InvokeAdhocReq
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body

interface HubAdhocService {
    fun submit(req: InvokeAdhocReq): HubSubmittedReqWithId
}

internal class DefaultHubAdhocService(
    private val template: HttpTemplate
) : HubAdhocService {
    override fun submit(req: InvokeAdhocReq): HubSubmittedReqWithId {
        return template
            .post("/v1/adhoc")
            .body(req)
            .execute(HubSubmittedReqWithId::class)
    }
}