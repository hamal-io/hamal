package io.hamal.core.adapter.hook

import io.hamal.core.adapter.security.FilterAccessPort
import io.hamal.repository.api.Hook
import io.hamal.repository.api.HookQueryRepository
import io.hamal.repository.api.HookQueryRepository.HookQuery
import org.springframework.stereotype.Component

fun interface HookListPort {
    operator fun invoke(query: HookQuery): List<Hook>
}

@Component
class HookListAdapter(
    private val hookQueryRepository: HookQueryRepository,
    private val filterAccess: FilterAccessPort
) : HookListPort {
    override fun invoke(query: HookQuery): List<Hook> = filterAccess(hookQueryRepository.list(query))
}
