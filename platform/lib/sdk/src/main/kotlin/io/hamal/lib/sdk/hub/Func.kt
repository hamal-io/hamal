package io.hamal.lib.sdk.hub

import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.kua.type.CodeType
import io.hamal.lib.sdk.fold
import io.hamal.request.CreateFuncReq
import io.hamal.request.InvokeFuncReq
import io.hamal.request.UpdateFuncReq
import kotlinx.serialization.Serializable

@Serializable
data class HubCreateFuncReq(
    override val namespaceId: NamespaceId? = null,
    override val name: FuncName,
    override val inputs: FuncInputs,
    override val code: CodeType
) : CreateFuncReq

@Serializable
data class HubUpdateFuncReq(
    override val namespaceId: NamespaceId? = null,
    override val name: FuncName? = null,
    override val inputs: FuncInputs? = null,
    override val code: CodeType? = null
) : UpdateFuncReq

@Serializable
data class HubInvokeFuncReq(
    override val correlationId: CorrelationId? = null,
    override val inputs: InvocationInputs? = null,
) : InvokeFuncReq

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
    fun create(groupId: GroupId, createFuncReq: HubCreateFuncReq): HubSubmittedReqWithId
    fun list(groupId: GroupId): List<HubFuncList.Func>
    fun get(funcId: FuncId): HubFunc
}

internal class DefaultHubFuncService(
    private val template: HttpTemplate
) : HubFuncService {

    override fun create(groupId: GroupId, createFuncReq: HubCreateFuncReq) =
        template.post("/v1/groups/{groupId}/funcs")
            .path("groupId", groupId)
            .body(createFuncReq)
            .execute()
            .fold(HubSubmittedReqWithId::class)

    override fun list(groupId: GroupId): List<HubFuncList.Func> =
        template.get("/v1/groups/{groupId}/funcs")
            .path("groupId", groupId)
            .execute()
            .fold(HubFuncList::class)
            .funcs

    override fun get(funcId: FuncId) =
        template.get("/v1/funcs/{funcId}")
            .path("funcId", funcId)
            .execute()
            .fold(HubFunc::class)

}