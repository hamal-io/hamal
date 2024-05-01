package io.hamal.repository.api

import io.hamal.lib.common.TreeNode
import io.hamal.lib.common.domain.*
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.NamespaceTreeId
import io.hamal.lib.domain.vo.NamespaceTreeId.Companion.NamespaceTreeId
import io.hamal.lib.domain.vo.WorkspaceId

data class NamespaceSubTree(
    val cmdId: CmdId,
    val id: NamespaceTreeId,
    override val workspaceId: WorkspaceId,
    val root: TreeNode<NamespaceId>
) : HasWorkspaceId {
    val values by lazy { root.preorder() }
}

data class NamespaceTree(
    val cmdId: CmdId,
    override val id: NamespaceTreeId,
    override val updatedAt: UpdatedAt,
    val workspaceId: WorkspaceId,
    val root: TreeNode<NamespaceId>
) : DomainObject<NamespaceTreeId>

interface NamespaceTreeRepository : NamespaceTreeCmdRepository, NamespaceTreeQueryRepository

interface NamespaceTreeCmdRepository : CmdRepository {

    fun create(cmd: CreateCmd): NamespaceTree

    fun append(cmd: AppendCmd): NamespaceTree

    data class CreateCmd(
        val id: CmdId,
        val treeId: NamespaceTreeId,
        val workspaceId: WorkspaceId,
        val rootNodeId: NamespaceId
    )

    data class AppendCmd(
        val id: CmdId,
        val treeId: NamespaceTreeId,
        val parentId: NamespaceId,
        val namespaceId: NamespaceId,
    )
}

interface NamespaceTreeQueryRepository {
    fun get(namespaceId: NamespaceId) = find(namespaceId) ?: throw NoSuchElementException("Namespace not found")
    fun find(namespaceId: NamespaceId): NamespaceTree?

    fun list(query: NamespaceTreeQuery): List<NamespaceTree>
    fun count(query: NamespaceTreeQuery): Count

    data class NamespaceTreeQuery(
        var afterId: NamespaceTreeId = NamespaceTreeId(SnowflakeId(Long.MAX_VALUE)),
        var limit: Limit = Limit(1),
        var workspaceIds: List<WorkspaceId> = listOf(),
        var treeIds: List<NamespaceTreeId> = listOf()
    )

}