package io.hamal.lib.sdk.hub

import io.hamal.lib.domain.req.CreateFuncReq
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.kua.type.CodeType
import io.hamal.lib.sdk.fold
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