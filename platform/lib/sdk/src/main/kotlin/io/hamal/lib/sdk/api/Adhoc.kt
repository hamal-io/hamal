package io.hamal.lib.sdk.api

import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.http.HttpTemplateImpl
import io.hamal.lib.http.body
import io.hamal.lib.sdk.fold
import io.hamal.request.InvokeAdhocReq
import kotlinx.serialization.Serializable

@Serializable
data class ApiInvokeAdhocReq(
    override val inputs: InvocationInputs,
    override val code: CodeValue
) : InvokeAdhocReq

interface ApiAdhocService {
    operator fun invoke(groupId: GroupId, req: ApiInvokeAdhocReq): ApiSubmittedReqWithId
}

internal class ApiAdhocServiceImpl(
    private val template: HttpTemplateImpl
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