package io.hamal.core.adapter.blueprint

import io.hamal.lib.domain.vo.BlueprintId
import io.hamal.repository.api.Blueprint
import io.hamal.repository.api.BlueprintQueryRepository
import org.springframework.stereotype.Component

fun interface BlueprintGetPort {
    operator fun invoke(blueprintId: BlueprintId): Blueprint
}

@Component
class BlueprintGetAdapter(
    private val blueprintQueryRepository: BlueprintQueryRepository
) : BlueprintGetPort {
    override fun invoke(blueprintId: BlueprintId): Blueprint = blueprintQueryRepository.get(blueprintId)
}
