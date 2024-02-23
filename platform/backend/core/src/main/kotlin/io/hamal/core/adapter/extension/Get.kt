package io.hamal.core.adapter.extension

import io.hamal.lib.domain.vo.ExtensionId
import io.hamal.repository.api.Extension
import io.hamal.repository.api.ExtensionQueryRepository
import org.springframework.stereotype.Component

fun interface ExtensionGetPort {
    operator fun invoke(extensionId: ExtensionId): Extension
}

@Component
class ExtensionGetAdapter(
    private val extensionQueryRepository: ExtensionQueryRepository
) : ExtensionGetPort {
    override fun invoke(extensionId: ExtensionId): Extension = extensionQueryRepository.get(extensionId)

}