package io.hamal.core.adapter.security

import io.hamal.core.security.SecurityContext
import io.hamal.lib.common.domain.DomainObject
import io.hamal.repository.api.*
import org.springframework.stereotype.Component

interface EnsureAccessPort {

    /**
     * Ensures that the current authentication in the SecurityContext has access to the domain object
     * @throws IllegalAccessError
     */
    operator fun <T : DomainObject<*>> invoke(obj: T): T
}

@Component
internal class EnsureAccessAdapter(
    workspaceQueryRepository: WorkspaceQueryRepository,
    namespaceTreeQueryRepository: NamespaceTreeQueryRepository
) : BaseAccess(workspaceQueryRepository, namespaceTreeQueryRepository), EnsureAccessPort {

    // FIXME this implementation is not optimal - replace it later
    override fun <T : DomainObject<*>> invoke(obj: T): T {
        val current = SecurityContext.current
        // FIXME not sure about this
        if (current is Auth.Runner || current is Auth.System) {
            return obj
        }

        if (current is Auth.Anonymous) {
            accessDenied(obj)
        }

        // FIXME
        if (current is Auth.ExecToken) {
            return obj
        }

        if (current is Auth.Account) {
            return when {
                obj is HasAccountId && accessAllowed(current, obj.accountId) -> obj
                obj is HasWorkspaceId && accessAllowed(current, obj.workspaceId) -> obj
                obj is HasNamespaceId && accessAllowed(current, obj.namespaceId) -> obj
                else -> accessDenied(obj)
            }
        }
        TODO()
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

