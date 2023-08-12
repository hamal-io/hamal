package io.hamal.lib.sdk.domain

import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.FuncInputs
import io.hamal.lib.domain.vo.FuncName
import io.hamal.lib.kua.type.CodeType
import kotlinx.serialization.Serializable

@Serializable
data class ApiFuncList(
    val funcs: List<ApiSimpleFunc>
) {
    @Serializable
    data class ApiSimpleFunc(
        val id: FuncId, val name: FuncName
    )
}


@Serializable
data class ApiFunc(
    val id: FuncId,
    val name: FuncName,
    val inputs: FuncInputs,
    val code: CodeType
)