package io.hamal.lib.sdk.hub

import io.hamal.lib.domain.req.InvokeAdhocReq
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body

interface HubAdhocService {
    fun submit(groupId: GroupId, req: InvokeAdhocReq): HubSubmittedReqWithId
}

internal class DefaultHubAdhocService(
    private val template: HttpTemplate
) : HubAdhocService {
    override fun submit(groupId: GroupId, req: InvokeAdhocReq): HubSubmittedReqWithId {
        return template
            .post("/v1/groups/{groupId}/adhoc")
            .path("groupId", groupId)
            .body(req)
            .execute(HubSubmittedReqWithId::class)
    }
}