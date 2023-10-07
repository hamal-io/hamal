package io.hamal.lib.sdk.api

import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.kua.type.CodeType
import io.hamal.lib.sdk.fold
import io.hamal.request.InvokeAdhocReq
import kotlinx.serialization.Serializable

@Serializable
data class ApiInvokeAdhocReq(
    override val inputs: InvocationInputs,
    override val code: CodeType
) : InvokeAdhocReq

interface ApiAdhocService {
    operator fun invoke(groupId: GroupId, req: ApiInvokeAdhocReq): ApiSubmittedReqWithId
}

internal class ApiAdhocServiceImpl(
    private val template: HttpTemplate
) : ApiAdhocService {
    override fun invoke(groupId: GroupId, req: ApiInvokeAdhocReq): ApiSubmittedReqWithId {
        return template
            .post("/v1/groups/{groupId}/adhoc")
            .path("groupId", groupId)
            .body(req)
            .execute()
            .fold(ApiSubmittedReqWithId::class)
    }
}