package io.hamal.lib.sdk.domain

import io.hamal.lib.domain.vo.*
import kotlinx.serialization.Serializable

@Serializable
data class ListFuncsResponse(
    val funcs: List<Func>
) {
    @Serializable
    data class Func(
        val id: FuncId, val name: FuncName
    )
}


