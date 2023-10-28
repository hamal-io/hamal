package io.hamal.core.adapter

import io.hamal.core.req.SubmitRequest
import io.hamal.lib.domain.vo.HookId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.repository.api.Hook
import io.hamal.repository.api.HookQueryRepository
import io.hamal.repository.api.HookQueryRepository.HookQuery
import io.hamal.repository.api.Namespace
import io.hamal.repository.api.NamespaceQueryRepository
import io.hamal.repository.api.submitted_req.HookCreateSubmitted
import io.hamal.repository.api.submitted_req.HookUpdateSubmitted
import io.hamal.request.CreateHookReq
import io.hamal.request.UpdateHookReq
import org.springframework.stereotype.Component

interface HookCreatePort {
    operator fun <T : Any> invoke(
        namespaceId: NamespaceId,
        req: CreateHookReq,
        responseHandler: (HookCreateSubmitted) -> T
    ): T
}

interface HookGetPort {
    operator fun <T : Any> invoke(hookId: HookId, responseHandler: (Hook, Namespace) -> T): T
}

interface HookListPort {
    operator fun <T : Any> invoke(query: HookQuery, responseHandler: (List<Hook>, Map<NamespaceId, Namespace>) -> T): T
}

interface HookUpdatePort {
    operator fun <T : Any> invoke(
        hookId: HookId,
        req: UpdateHookReq,
        responseHandler: (HookUpdateSubmitted) -> T
    ): T
}

interface HookPort : HookCreatePort, HookGetPort, HookListPort, HookUpdatePort

@Component
class HookAdapter(
    private val submitRequest: SubmitRequest,
    private val hookQueryRepository: HookQueryRepository,
    private val namespaceQueryRepository: NamespaceQueryRepository
) : HookPort {
    override fun <T : Any> invoke(
        namespaceId: NamespaceId,
        req: CreateHookReq,
        responseHandler: (HookCreateSubmitted) -> T
    ): T {
        return responseHandler(submitRequest(namespaceId, req))
    }

    override fun <T : Any> invoke(hookId: HookId, responseHandler: (Hook, Namespace) -> T): T {
        val hook = hookQueryRepository.get(hookId)
        val namespaces = namespaceQueryRepository.get(hook.namespaceId)
        return responseHandler(hook, namespaces)
    }


    override fun <T : Any> invoke(
        query: HookQuery,
        responseHandler: (List<Hook>, Map<NamespaceId, Namespace>) -> T
    ): T {
        val hooks = hookQueryRepository.list(query)
        val namespaces = namespaceQueryRepository.list(hooks.map(Hook::namespaceId))
            .associateBy(Namespace::id)
        return responseHandler(hooks, namespaces)
    }

    override fun <T : Any> invoke(
        hookId: HookId,
        req: UpdateHookReq,
        responseHandler: (HookUpdateSubmitted) -> T
    ): T {
        ensureHookExists(hookId)
        return responseHandler(submitRequest(hookId, req))
    }

    private fun ensureHookExists(hookId: HookId) {
        hookQueryRepository.get(hookId)
    }
}