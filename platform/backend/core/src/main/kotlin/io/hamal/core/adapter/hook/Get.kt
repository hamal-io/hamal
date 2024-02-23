package io.hamal.core.adapter.hook

import io.hamal.lib.domain.vo.HookId
import io.hamal.repository.api.Hook
import io.hamal.repository.api.HookQueryRepository
import org.springframework.stereotype.Component

fun interface HookGetPort {
    operator fun invoke(hookId: HookId): Hook
}

@Component
class HookGetAdapter(
    private val hookQueryRepository: HookQueryRepository
) : HookGetPort {
    override fun invoke(hookId: HookId): Hook = hookQueryRepository.get(hookId)
}