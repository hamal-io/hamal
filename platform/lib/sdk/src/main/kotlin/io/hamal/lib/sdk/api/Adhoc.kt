package io.hamal.lib.sdk.api

import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.http.HttpTemplateImpl
import io.hamal.lib.http.body
import io.hamal.lib.sdk.foldReq
import io.hamal.request.InvokeAdhocReq
import kotlinx.serialization.Serializable

@Serializable
data class ApiAdhocInvokeReq(
    override val inputs: InvocationInputs,
    override val code: CodeValue
) : InvokeAdhocReq

interface ApiAdhocService {
    operator fun invoke(namespaceId: NamespaceId, req: ApiAdhocInvokeReq): ApiSubmittedReqImpl<ExecId>
}

internal class ApiAdhocServiceImpl(
    private val template: HttpTemplateImpl
) : ApiAdhocService {
    override fun invoke(namespaceId: NamespaceId, req: ApiAdhocInvokeReq): ApiSubmittedReqImpl<ExecId> {
        return template
            .post("/v1/namespaces/{namespaceId}/adhoc")
            .path("namespaceId", namespaceId)
            .body(req)
            .execute()
            .foldReq()
    }
}