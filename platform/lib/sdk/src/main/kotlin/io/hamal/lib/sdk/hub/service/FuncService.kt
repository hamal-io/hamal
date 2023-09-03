package io.hamal.lib.sdk.hub.service

import io.hamal.lib.domain.req.CreateFuncReq
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.sdk.fold
import io.hamal.lib.sdk.hub.domain.ApiSubmittedReqWithId

interface FuncService {
    fun create(createFuncReq: CreateFuncReq): ApiSubmittedReqWithId
}

internal class DefaultFuncService(
    private val template: HttpTemplate
) : FuncService {
    override fun create(createFuncReq: CreateFuncReq) =
        template.post("/v1/funcs")
            .body(createFuncReq)
            .execute()
            .fold(ApiSubmittedReqWithId::class)

}