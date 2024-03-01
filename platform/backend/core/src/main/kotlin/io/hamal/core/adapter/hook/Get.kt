package io.hamal.core.adapter.hook

import io.hamal.core.adapter.security.EnsureAccessPort
import io.hamal.lib.domain.vo.HookId
import io.hamal.repository.api.Hook
import io.hamal.repository.api.HookQueryRepository
import org.springframework.stereotype.Component

fun interface HookGetPort {
    operator fun invoke(hookId: HookId): Hook
}

@Component
class HookGetAdapter(
    private val hookQueryRepository: HookQueryRepository,
    private val ensureAccess: EnsureAccessPort
) : HookGetPort {
    override fun invoke(hookId: HookId): Hook = ensureAccess(hookQueryRepository.get(hookId))
}