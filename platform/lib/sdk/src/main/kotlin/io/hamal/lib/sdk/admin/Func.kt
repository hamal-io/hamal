package io.hamal.lib.sdk.admin

import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.kua.type.CodeType
import io.hamal.lib.sdk.fold
import kotlinx.serialization.Serializable

@Serializable
data class AdminCreateFuncReq(
    val namespaceId: NamespaceId? = null,
    val name: FuncName,
    val inputs: FuncInputs,
    val code: CodeType
)

@Serializable
data class AdminUpdateFuncReq(
    val namespaceId: NamespaceId? = null,
    val name: FuncName? = null,
    val inputs: FuncInputs? = null,
    val code: CodeType? = null
)

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
    fun list(groupId: GroupId): List<AdminFuncList.Func>
    fun get(funcId: FuncId): AdminFunc
}

internal class DefaultAdminFuncService(
    private val template: HttpTemplate
) : AdminFuncService {

    override fun create(groupId: GroupId, createFuncReq: AdminCreateFuncReq) =
        template.post("/v1/groups/{groupId}/funcs")
            .path("groupId", groupId)
            .body(createFuncReq)
            .execute()
            .fold(AdminSubmittedReqWithId::class)

    override fun list(groupId: GroupId): List<AdminFuncList.Func> =
        template.get("/v1/groups/{groupId}/funcs")
            .path("groupId", groupId)
            .execute()
            .fold(AdminFuncList::class)
            .funcs

    override fun get(funcId: FuncId) =
        template.get("/v1/funcs/{funcId}")
            .path("funcId", funcId)
            .execute()
            .fold(AdminFunc::class)

}