package io.hamal.lib.sdk.domain

import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.type.CodeType
import kotlinx.serialization.Serializable

@Serializable
data class ApiFuncList(
    val funcs: List<ApiSimpleFunc>
) {
    @Serializable
    data class ApiSimpleFunc(
        val id: FuncId,
        val namespace: Namespace,
        val name: FuncName
    ) {
        @Serializable
        data class Namespace(
            val id: NamespaceId,
            val name: NamespaceName
        )
    }
}


@Serializable
data class ApiFunc(
    val id: FuncId,
    val namespace: Namespace,
    val name: FuncName,
    val inputs: FuncInputs,
    val code: CodeType
) {
    @Serializable
    data class Namespace(
        val id: NamespaceId,
        val name: NamespaceName
    )
}