package io.hamal.core.adapter

import io.hamal.lib.domain.GenerateId
import io.hamal.lib.domain._enum.RequestStatus.Submitted
import io.hamal.lib.domain.request.HookCreateRequest
import io.hamal.lib.domain.request.HookCreateRequested
import io.hamal.lib.domain.request.HookUpdateRequest
import io.hamal.lib.domain.request.HookUpdateRequested
import io.hamal.lib.domain.vo.HookId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.RequestId
import io.hamal.repository.api.Hook
import io.hamal.repository.api.HookQueryRepository
import io.hamal.repository.api.HookQueryRepository.HookQuery
import io.hamal.repository.api.NamespaceQueryRepository
import io.hamal.repository.api.RequestCmdRepository
import org.springframework.stereotype.Component

interface HookCreatePort {
    operator fun invoke(namespaceId: NamespaceId, req: HookCreateRequest): HookCreateRequested
}

interface HookGetPort {
    operator fun invoke(hookId: HookId): Hook
}

interface HookListPort {
    operator fun invoke(query: HookQuery): List<Hook>
}

interface HookUpdatePort {
    operator fun invoke(hookId: HookId, req: HookUpdateRequest): HookUpdateRequested
}

interface HookPort : HookCreatePort, HookGetPort, HookListPort, HookUpdatePort

@Component
class HookAdapter(
    private val generateDomainId: GenerateId,
    private val hookQueryRepository: HookQueryRepository,
    private val namespaceQueryRepository: NamespaceQueryRepository,
    private val requestCmdRepository: RequestCmdRepository
) : HookPort {
    override fun invoke(namespaceId: NamespaceId, req: HookCreateRequest): HookCreateRequested {
        val namespace = namespaceQueryRepository.get(namespaceId)
        return HookCreateRequested(
            id = generateDomainId(::RequestId),
            status = Submitted,
            hookId = generateDomainId(::HookId),
            workspaceId = namespace.workspaceId,
            namespaceId = namespace.id,
            name = req.name
        ).also(requestCmdRepository::queue)
    }

    override fun invoke(hookId: HookId): Hook = hookQueryRepository.get(hookId)

    override fun invoke(query: HookQuery): List<Hook> = hookQueryRepository.list(query)

    override fun invoke(hookId: HookId, req: HookUpdateRequest): HookUpdateRequested {
        ensureHookExists(hookId)
        return HookUpdateRequested(
            id = generateDomainId(::RequestId),
            status = Submitted,
            workspaceId = hookQueryRepository.get(hookId).workspaceId,
            hookId = hookId,
            name = req.name,
        ).also(requestCmdRepository::queue)
    }

    private fun ensureHookExists(hookId: HookId) {
        hookQueryRepository.get(hookId)
    }
}