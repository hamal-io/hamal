package io.hamal.core.adapter.func

import io.hamal.core.adapter.security.FilterAccessPort
import io.hamal.repository.api.Func
import io.hamal.repository.api.FuncQueryRepository
import io.hamal.repository.api.FuncQueryRepository.FuncQuery
import org.springframework.stereotype.Component


fun interface FuncListPort {
    operator fun invoke(query: FuncQuery): List<Func>
}

@Component
class FuncListAdapter(
    private val funcQueryRepository: FuncQueryRepository,
    private val filterAccess: FilterAccessPort
) : FuncListPort {
    override fun invoke(query: FuncQuery): List<Func> = filterAccess(funcQueryRepository.list(query))
}