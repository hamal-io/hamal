package io.hamal.core.component.func

import io.hamal.lib.domain.vo.FuncId
import io.hamal.repository.api.Func
import io.hamal.repository.api.FuncQueryRepository
import io.hamal.repository.api.Namespace
import io.hamal.repository.api.NamespaceQueryRepository
import org.springframework.stereotype.Component

@Component
class GetFunc(
    private val funcQueryRepository: FuncQueryRepository,
    private val namespaceQueryRepository: NamespaceQueryRepository
) {
    operator fun <T : Any> invoke(
        funcId: FuncId,
        responseHandler: (Func, Namespace) -> T
    ): T {
        val func = funcQueryRepository.get(funcId)
        val namespaces = namespaceQueryRepository.get(func.namespaceId)
        return responseHandler(func, namespaces)
    }
}