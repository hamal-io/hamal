package io.hamal.core.adapter.security

import io.hamal.core.security.SecurityContext
import io.hamal.lib.common.domain.DomainObject
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.WorkspaceId
import io.hamal.repository.api.*
import io.hamal.repository.api.Auth.Anonymous
import io.hamal.repository.api.NamespaceTreeQueryRepository.NamespaceTreeQuery
import io.hamal.repository.api.WorkspaceQueryRepository.WorkspaceQuery
import org.springframework.stereotype.Component

interface EnsureAccessPort {

    /**
     * Ensures that the current authentication in the SecurityContext has access to the domain object
     * @throws IllegalAccessError
     */
    operator fun <T : DomainObject<*>> invoke(obj: T): T
}

@Component
class EnsureAccessAdapter(
    private val workspaceQueryRepository: WorkspaceQueryRepository,
    private val namespaceTreeQueryRepository: NamespaceTreeQueryRepository
) : EnsureAccessPort {

    // FIXME this implementation is not optimal - replace it later
    override fun <T : DomainObject<*>> invoke(obj: T): T {
        val current = SecurityContext.current
        // FIXME not sure about this
        if (current is Auth.Runner || current is Auth.System) {
            return obj
        }

        if (current is Anonymous) {
            accessDenied(obj)
        }
        if (current is Auth.Account) {
            when {
                obj is HasAccountId && accessAllowed(current, obj.accountId) -> return obj
                obj is HasWorkspaceId && accessAllowed(current, obj.workspaceId) -> return obj
                obj is HasNamespaceId && accessAllowed(current, obj.namespaceId) -> return obj
                else -> accessDenied(obj)
            }
        }
        TODO()
    }

    internal fun accessAllowed(auth: Auth.Account, accountId: AccountId): Boolean {
        return auth.accountId == accountId
    }

    internal fun accessAllowed(auth: Auth.Account, workspaceId: WorkspaceId): Boolean {
        return workspaceQueryRepository.list(
            WorkspaceQuery(
                limit = Limit.all,
                accountIds = listOf(auth.accountId)
            )
        ).any { it.workspaceId == workspaceId }
    }

    internal fun accessAllowed(auth: Auth.Account, namespaceId: NamespaceId): Boolean {
        return namespaceTreeQueryRepository.list(NamespaceTreeQuery(
            limit = Limit.one,
            workspaceIds = workspaceQueryRepository.list(
                WorkspaceQuery(
                    limit = Limit.all,
                    accountIds = listOf(auth.accountId)
                )
            ).map { it.workspaceId }
        )).any { it.root.find { node -> node.value == namespaceId } != null }
    }

    private fun accessDenied(obj: DomainObject<*>): Nothing {
        val name = when (obj) {
            is Exec -> "Exec"
            is Topic -> "Topic"
            is Trigger -> "Trigger"
            else -> obj.javaClass.simpleName
        }
        throw IllegalAccessError("$name not found")
    }
}

