package io.hamal.core.adapter.func

import io.hamal.core.adapter.security.EnsureAccessPort
import io.hamal.lib.domain.vo.FuncId
import io.hamal.repository.api.Func
import io.hamal.repository.api.FuncQueryRepository
import org.springframework.stereotype.Component

fun interface FuncGetPort {
    operator fun invoke(funcId: FuncId): Func
}

@Component
class FuncGetAdapter(
    private val funcQueryRepository: FuncQueryRepository,
    private val ensureAccess: EnsureAccessPort
) : FuncGetPort {
    override fun invoke(funcId: FuncId): Func = ensureAccess(funcQueryRepository.get(funcId))

}