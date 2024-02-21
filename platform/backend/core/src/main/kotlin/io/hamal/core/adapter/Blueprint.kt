package io.hamal.core.adapter

import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.RequestStatus.Submitted
import io.hamal.lib.domain.request.BlueprintCreateRequest
import io.hamal.lib.domain.request.BlueprintCreateRequested
import io.hamal.lib.domain.request.BlueprintUpdateRequest
import io.hamal.lib.domain.request.BlueprintUpdateRequested
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.BlueprintId
import io.hamal.lib.domain.vo.RequestId
import io.hamal.repository.api.Blueprint
import io.hamal.repository.api.BlueprintQueryRepository
import io.hamal.repository.api.BlueprintQueryRepository.BlueprintQuery
import io.hamal.repository.api.RequestCmdRepository
import org.springframework.stereotype.Component

interface BlueprintCreatePort {
    operator fun invoke(
        accountId: AccountId,
        req: BlueprintCreateRequest,
    ): BlueprintCreateRequested
}

interface BlueprintGetPort {
    operator fun invoke(blueprintId: BlueprintId): Blueprint
}

interface BlueprintUpdatePort {
    operator fun invoke(
        bpId: BlueprintId,
        req: BlueprintUpdateRequest,
    ): BlueprintUpdateRequested
}

interface BlueprintListPort {
    operator fun invoke(
        query: BlueprintQuery,
    ): List<Blueprint>
}

interface BlueprintPort : BlueprintCreatePort, BlueprintGetPort, BlueprintUpdatePort, BlueprintListPort

@Component
class BlueprintAdapter(
    private val blueprintQueryRepository: BlueprintQueryRepository,
    private val generateDomainId: GenerateDomainId,
    private val requestCmdRepository: RequestCmdRepository
) : BlueprintPort {
    override fun invoke(
        accountId: AccountId,
        req: BlueprintCreateRequest,
    ): BlueprintCreateRequested {
        return BlueprintCreateRequested(
            id = generateDomainId(::RequestId),
            status = Submitted,
            blueprintId = generateDomainId(::BlueprintId),
            name = req.name,
            inputs = req.inputs,
            value = req.value,
            creatorId = accountId,
            description = req.description
        ).also(requestCmdRepository::queue)
    }

    override fun invoke(blueprintId: BlueprintId): Blueprint = blueprintQueryRepository.get(blueprintId)

    override fun invoke(
        bpId: BlueprintId,
        req: BlueprintUpdateRequest,
    ): BlueprintUpdateRequested {
        ensureBlueprintExists(bpId)
        return BlueprintUpdateRequested(
            id = generateDomainId(::RequestId),
            status = Submitted,
            blueprintId = bpId,
            name = req.name,
            inputs = req.inputs,
            value = req.value,
            description = req.description

        ).also(requestCmdRepository::queue)
    }

    override fun invoke(query: BlueprintQuery): List<Blueprint> = blueprintQueryRepository.list(query)

    private fun ensureBlueprintExists(blueprintId: BlueprintId) = blueprintQueryRepository.get(blueprintId)
}