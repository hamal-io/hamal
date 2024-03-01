package io.hamal.repository.api

import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.WorkspaceId

interface HasAccountId {
    val accountId: AccountId
}

interface HasWorkspaceId {
    val workspaceId: WorkspaceId
}

interface HasNamespaceId {
    val namespaceId: NamespaceId
}