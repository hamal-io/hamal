package io.hamal.lib.sdk.domain

import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.FuncName
import kotlinx.serialization.Serializable


@Serializable
data class ApiListFuncResponse(
    val funcs: List<Func>
) {

    @Serializable
    data class Func(
        val id: FuncId,
        val name: FuncName
    )
}

