package io.hamal.core.component.func

import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.repository.api.Func
import io.hamal.repository.api.FuncQueryRepository
import io.hamal.repository.api.FuncQueryRepository.FuncQuery
import io.hamal.repository.api.Namespace
import io.hamal.repository.api.NamespaceQueryRepository
import org.springframework.stereotype.Component

@Component
class ListFunc(
    private val funcQueryRepository: FuncQueryRepository,
    private val namespaceQueryRepository: NamespaceQueryRepository
) {
    operator fun <T : Any> invoke(
        query: FuncQuery,
        responseHandler: (List<Func>, Map<NamespaceId, Namespace>) -> T
    ): T {
        val funcs = funcQueryRepository.list(query)
        val namespaces = namespaceQueryRepository.list(funcs.map(Func::namespaceId)).associateBy(Namespace::id)
        return responseHandler(funcs, namespaces)
    }
}