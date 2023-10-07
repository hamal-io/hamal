package io.hamal.lib.sdk.admin

import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.kua.type.CodeType
import io.hamal.lib.sdk.fold
import io.hamal.request.CreateFuncReq
import io.hamal.request.UpdateFuncReq
import kotlinx.serialization.Serializable

@Serializable
data class AdminCreateFuncReq(
    override val namespaceId: NamespaceId? = null,
    override val name: FuncName,
    override val inputs: FuncInputs,
    override val code: CodeType
) : CreateFuncReq

@Serializable
data class AdminUpdateFuncReq(
    override val namespaceId: NamespaceId? = null,
    override val name: FuncName? = null,
    override val inputs: FuncInputs? = null,
    override val code: CodeType? = null
) : UpdateFuncReq

@Serializable
data class AdminInvokeFuncReq(
    val correlationId: CorrelationId? = null,
    val inputs: InvocationInputs? = null,
)

@Serializable
data class AdminFuncList(
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
data class AdminFunc(
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

interface AdminFuncService {
    fun create(groupId: GroupId, createFuncReq: AdminCreateFuncReq): AdminSubmittedReqWithId
    fun list(groupIds: List<GroupId> = listOf()): List<AdminFuncList.Func>
    fun get(funcId: FuncId): AdminFunc
}

internal class AdminFuncServiceImpl(
    private val template: HttpTemplate
) : AdminFuncService {

    override fun create(groupId: GroupId, createFuncReq: AdminCreateFuncReq) =
        template.post("/v1/groups/{groupId}/funcs")
            .path("groupId", groupId)
            .body(createFuncReq)
            .execute()
            .fold(AdminSubmittedReqWithId::class)

    override fun list(groupIds: List<GroupId>): List<AdminFuncList.Func> =
        template.get("/v1/funcs")
            .parameter("group_id", groupIds)
            .execute()
            .fold(AdminFuncList::class)
            .funcs

    override fun get(funcId: FuncId) =
        template.get("/v1/funcs/{funcId}")
            .path("funcId", funcId)
            .execute()
            .fold(AdminFunc::class)

}