package io.hamal.repository.api

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.DomainObject
import io.hamal.lib.common.domain.UpdatedAt
import io.hamal.lib.domain.vo.FlowId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.WorkspaceId

data class Flow(
    override val id: FlowId,
    override val updatedAt: UpdatedAt,
    override val workspaceId: WorkspaceId,
    override val namespaceId: NamespaceId,
    val cmdId: CmdId
) : DomainObject<FlowId>, HasNamespaceId, HasWorkspaceId