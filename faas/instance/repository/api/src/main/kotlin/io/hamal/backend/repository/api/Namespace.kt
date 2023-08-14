package io.hamal.backend.repository.api

import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.DomainObject
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.NamespaceInputs
import io.hamal.lib.domain.vo.NamespaceName
import kotlinx.serialization.Serializable

interface NamespaceCmdRepository {
    fun create(cmd: CreateCmd): Namespace

    fun update(namespaceId: NamespaceId, cmd: UpdateCmd): Namespace

    fun clear()

    data class CreateCmd(
        val id: CmdId,
        val namespaceId: NamespaceId,
        val name: NamespaceName,
        val inputs: NamespaceInputs
    )

    data class UpdateCmd(
        val id: CmdId,
        val name: NamespaceName,
        val inputs: NamespaceInputs
    )
}

interface NamespaceQueryRepository {
    fun get(namespaceId: NamespaceId) = find(namespaceId) ?: throw NoSuchElementException("Namespace not found")
    fun find(namespaceId: NamespaceId): Namespace?
    fun list(block: NamespaceQuery.() -> Unit): List<Namespace>
    data class NamespaceQuery(
        var afterId: NamespaceId = NamespaceId(SnowflakeId(Long.MAX_VALUE)),
        var limit: Limit = Limit(1)
    )
}

@Serializable
data class Namespace(
    val cmdId: CmdId,
    override val id: NamespaceId,
    val name: NamespaceName,
    val inputs: NamespaceInputs
) : DomainObject<NamespaceId>
