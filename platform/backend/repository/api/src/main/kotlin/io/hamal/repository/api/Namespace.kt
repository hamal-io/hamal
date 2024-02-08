package io.hamal.repository.api

import io.hamal.lib.common.domain.*
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.vo.*

data class Namespace(
    val cmdId: CmdId,
    override val id: NamespaceId,
    override val updatedAt: UpdatedAt,
    val groupId: GroupId,
    val type: NamespaceType,
    val name: NamespaceName,
    val inputs: NamespaceInputs
) : DomainObject<NamespaceId>


interface NamespaceRepository : NamespaceCmdRepository, NamespaceQueryRepository

interface NamespaceCmdRepository : CmdRepository {
    fun create(cmd: CreateCmd): Namespace

    fun update(namespaceId: NamespaceId, cmd: UpdateCmd): Namespace

    data class CreateCmd(
        val id: CmdId,
        val namespaceId: NamespaceId,
        val groupId: GroupId,
        val type: NamespaceType? = NamespaceType.default,
        val name: NamespaceName,
        val inputs: NamespaceInputs
    )

    data class UpdateCmd(
        val id: CmdId,
        val name: NamespaceName? = null,
        val inputs: NamespaceInputs? = null
    )
}

interface NamespaceQueryRepository {
    fun get(namespaceId: NamespaceId) = find(namespaceId) ?: throw NoSuchElementException("Namespace not found")
    fun get(namespaceName: NamespaceName) = find(namespaceName) ?: throw NoSuchElementException("Namespace not found")

    fun find(namespaceId: NamespaceId): Namespace?
    fun find(namespaceName: NamespaceName): Namespace?

    fun list(query: NamespaceQuery): List<Namespace>
    fun list(namespaceIds: List<NamespaceId>) = list(
        NamespaceQuery(
            limit = Limit.all,
            groupIds = listOf(),
            namespaceIds = namespaceIds,
        )
    )

    fun count(query: NamespaceQuery): Count

    data class NamespaceQuery(
        var afterId: NamespaceId = NamespaceId(SnowflakeId(Long.MAX_VALUE)),
        var limit: Limit = Limit(1),
        var namespaceIds: List<NamespaceId> = listOf(),
        var groupIds: List<GroupId> = listOf()
    )
}
