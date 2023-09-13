package io.hamal.lib.sdk.hub

import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.kua.type.CodeType
import io.hamal.lib.sdk.fold
import io.hamal.request.InvokeAdhocReq
import kotlinx.serialization.Serializable

@Serializable
data class HubInvokeAdhocReq(
    override val inputs: InvocationInputs,
    override val code: CodeType
) : InvokeAdhocReq

interface HubAdhocService {
    operator fun invoke(groupId: GroupId, req: HubInvokeAdhocReq): HubSubmittedReqWithId
}

internal class DefaultHubAdhocService(
    private val template: HttpTemplate
) : HubAdhocService {
    override fun invoke(groupId: GroupId, req: HubInvokeAdhocReq): HubSubmittedReqWithId {
        return template
            .post("/v1/groups/{groupId}/adhoc")
            .path("groupId", groupId)
            .body(req)
            .execute()
            .fold(HubSubmittedReqWithId::class)
    }
}