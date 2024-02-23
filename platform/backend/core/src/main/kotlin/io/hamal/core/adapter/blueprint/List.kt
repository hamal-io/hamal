package io.hamal.core.adapter.blueprint

import io.hamal.repository.api.Blueprint
import io.hamal.repository.api.BlueprintQueryRepository
import io.hamal.repository.api.BlueprintQueryRepository.BlueprintQuery
import org.springframework.stereotype.Component

fun interface BlueprintListPort {
    operator fun invoke(query: BlueprintQuery): List<Blueprint>
}

@Component
class BlueprintListAdapter(
    private val blueprintQueryRepository: BlueprintQueryRepository
) : BlueprintListPort {
    override fun invoke(query: BlueprintQuery): List<Blueprint> = blueprintQueryRepository.list(query)
}