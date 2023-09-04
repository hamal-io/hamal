package io.hamal.lib.sdk.hub

import io.hamal.lib.domain.req.CreateFuncReq
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.kua.type.CodeType
import io.hamal.lib.sdk.fold
import io.hamal.lib.sdk.hub.HubFuncList.Func
import kotlinx.serialization.Serializable

@Serializable
data class HubFuncList(
    val funcs: List<Func>
) {
    @Serializable
    data class Func(
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
data class HubFunc(
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

interface HubFuncService {
    fun create(createFuncReq: CreateFuncReq): HubSubmittedReqWithId
    fun list(): List<Func>
    fun get(funcId: FuncId): HubFunc
}

internal class DefaultHubFuncService(
    private val template: HttpTemplate
) : HubFuncService {

    override fun create(createFuncReq: CreateFuncReq) =
        template.post("/v1/funcs")
            .body(createFuncReq)
            .execute()
            .fold(HubSubmittedReqWithId::class)

    override fun list(): List<Func> =
        template.get("/v1/funcs")
            .execute()
            .fold(HubFuncList::class)
            .funcs

    override fun get(funcId: FuncId) =
        template.get("/v1/funcs/{funcId}")
            .path("funcId", funcId)
            .execute()
            .fold(HubFunc::class)

}