package io.hamal.lib.sdk.api

import io.hamal.lib.domain.vo.CodeId
import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.domain.vo.CodeVersion
import io.hamal.lib.http.HttpTemplate
import kotlinx.serialization.Serializable

@Serializable
data class ApiCode(
    val id: CodeId,
    val value: CodeValue,
    val version: CodeVersion
)

interface ApiCodeService {
    fun get(codeId: CodeId): ApiCode
    fun get(codeId: CodeId, codeVersion: CodeVersion): ApiCode

}

internal class ApiCodeServiceImpl(
    private val template: HttpTemplate
) : ApiCodeService {
    override fun get(codeId: CodeId): ApiCode {
        TODO("Not yet implemented")
    }

    override fun get(codeId: CodeId, codeVersion: CodeVersion): ApiCode {
        TODO("Not yet implemented")
    }
}