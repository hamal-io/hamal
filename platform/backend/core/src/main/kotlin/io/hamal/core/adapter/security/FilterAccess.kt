package io.hamal.core.adapter.security

import io.hamal.core.security.SecurityContext
import io.hamal.lib.common.domain.DomainObject
import io.hamal.repository.api.*
import org.springframework.stereotype.Component

interface FilterAccessPort {
    /**
     * Filters out all objects which the current authentication in SecurityContext has no access to
     */
    operator fun <T : DomainObject<*>> invoke(objs: List<T>): List<T>

    operator fun <T : DomainObject<*>> invoke(obj: T): T?
}

@Component
internal class FilterAccessAdapter(
    workspaceQueryRepository: WorkspaceQueryRepository,
    namespaceTreeQueryRepository: NamespaceTreeQueryRepository
) : BaseAccess(workspaceQueryRepository, namespaceTreeQueryRepository), FilterAccessPort {

    override fun <T : DomainObject<*>> invoke(objs: List<T>): List<T> {
        val current = SecurityContext.current
        // FIXME not sure about this
        if (current is Auth.Runner || current is Auth.System) {
            return objs
        }

        if (current is Auth.Anonymous) {
            return listOf()
        }

        return objs.filter { obj ->
            if (current is Auth.Account) {
                when {
                    obj is HasAccountId && accessAllowed(current, obj.accountId) -> true
                    obj is HasWorkspaceId && accessAllowed(current, obj.workspaceId) -> true
                    obj is HasNamespaceId && accessAllowed(current, obj.namespaceId) -> true
                    else -> false
                }
            } else {
                TODO()
            }
        }
    }

    override fun <T : DomainObject<*>> invoke(obj: T): T? {
        val current = SecurityContext.current
        // FIXME not sure about this
        if (current is Auth.Runner || current is Auth.System) {
            return obj
        }

        if (current is Auth.Anonymous) {
            return null
        }

        return if (current is Auth.Account) {
            when {
                obj is HasAccountId && accessAllowed(current, obj.accountId) -> obj
                obj is HasWorkspaceId && accessAllowed(current, obj.workspaceId) -> obj
                obj is HasNamespaceId && accessAllowed(current, obj.namespaceId) -> obj
                else -> null
            }
        } else {
            TODO()
        }
    }
}