package io.hamal.core.adapter.extension

import io.hamal.repository.api.Extension
import io.hamal.repository.api.ExtensionQueryRepository
import io.hamal.repository.api.ExtensionQueryRepository.ExtensionQuery
import org.springframework.stereotype.Component

fun interface ExtensionListPort {
    operator fun invoke(query: ExtensionQuery): List<Extension>
}

@Component
class ExtensionListAdapter(
    private val extensionQueryRepository: ExtensionQueryRepository
) : ExtensionListPort {
    override fun invoke(query: ExtensionQuery): List<Extension> = extensionQueryRepository.list(query)
}