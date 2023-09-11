package io.hamal.lib.sdk.admin

import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.kua.type.CodeType
import io.hamal.lib.sdk.fold
import io.hamal.request.adhoc.InvokeAdhocReq
import kotlinx.serialization.Serializable

@Serializable
data class AdminInvokeAdhocReq(
    override val inputs: InvocationInputs,
    override val code: CodeType
) : InvokeAdhocReq

interface AdminAdhocService {
    operator fun invoke(groupId: GroupId, req: AdminInvokeAdhocReq): AdminSubmittedReqWithId
}

internal class DefaultAdminAdhocService(
    private val template: HttpTemplate
) : AdminAdhocService {
    override fun invoke(groupId: GroupId, req: AdminInvokeAdhocReq): AdminSubmittedReqWithId {
        return template
            .post("/v1/groups/{groupId}/adhoc")
            .path("groupId", groupId)
            .body(req)
            .execute()
            .fold(AdminSubmittedReqWithId::class)
    }
}