package io.hamal.lib.sdk.api

import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpTemplate
import io.hamal.request.CreateExtensionReq
import io.hamal.request.UpdateExtensionReq
import kotlinx.serialization.Serializable

@Serializable
data class ApiExtension(
    val id: ExtensionId,
    val name: ExtensionName,
    val code: ExtensionCode
) {
    @Serializable
    data class ExtensionCode(
        val id: CodeId,
        val version: CodeVersion
    )
}

@Serializable
data class ApiExtensionList(
    val extensions: List<Extension>
) {
    @Serializable
    data class Extension(
        val id: ExtensionId,
        val name: ExtensionName,
    )
}

@Serializable
data class ApiCreateExtensionReq(
    override val name: ExtensionName,
    override val codeId: CodeId,
    override val codeVersion: CodeVersion
) : CreateExtensionReq

@Serializable
data class ApiUpdateExtensionReq(
    override val name: ExtensionName? = null,
    override val codeId: CodeId? = null,
    override val codeVersion: CodeVersion? = null
) : UpdateExtensionReq


interface ApiExtensionService {
    fun create(groupId: GroupId, createExtReq: ApiCreateExtensionReq): ApiSubmittedReqWithId
    fun get(extId: ExtensionId): ApiExtension
    fun list(): List<ApiExtension>
    fun update(extId: ExtensionId, updateExtReq: ApiUpdateExtensionReq): ApiSubmittedReqWithId
}


internal class ApiExtensionServiceImpl(
    private val template: HttpTemplate
) : ApiExtensionService {
    override fun create(groupId: GroupId, createExtReq: ApiCreateExtensionReq): ApiSubmittedReqWithId {
        TODO("Not yet implemented")
    }

    override fun get(extId: ExtensionId): ApiExtension {
        TODO("Not yet implemented")
    }

    override fun list(): List<ApiExtension> {
        TODO("Not yet implemented")
    }

    override fun update(extId: ExtensionId, updateExtReq: ApiUpdateExtensionReq): ApiSubmittedReqWithId {
        TODO("Not yet implemented")
    }
}