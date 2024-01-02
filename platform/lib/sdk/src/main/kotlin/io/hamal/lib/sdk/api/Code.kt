package io.hamal.lib.sdk.api

import io.hamal.lib.domain.vo.CodeId
import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.domain.vo.CodeVersion
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.sdk.fold

data class ApiCode(
    val id: CodeId,
    val value: CodeValue,
    val version: CodeVersion
)

interface ApiCodeService {
    fun get(codeId: CodeId, codeVersion: CodeVersion? = null): ApiCode
}

internal class ApiCodeServiceImpl(
    private val template: HttpTemplate
) : ApiCodeService {

    override fun get(codeId: CodeId, codeVersion: CodeVersion?): ApiCode {
        return template.get("/v1/code/{codeId}")
            .path("codeId", codeId)
            .let { builder ->
                if (codeVersion != null) {
                    builder.parameter("code_version", codeVersion.value)
                } else {
                    builder
                }
            }
            .execute()
            .fold(ApiCode::class)
    }
}