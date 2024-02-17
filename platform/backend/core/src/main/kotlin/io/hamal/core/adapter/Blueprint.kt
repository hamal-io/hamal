package io.hamal.core.adapter

import io.hamal.lib.domain.GenerateId
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
    operator fun <T : Any> invoke(
        accountId: AccountId,
        req: BlueprintCreateRequest,
        responseHandler: (BlueprintCreateRequested) -> T
    ): T
}

interface BlueprintGetPort {
    operator fun <T : Any> invoke(blueprintId: BlueprintId, responseHandler: (Blueprint) -> T): T
}

interface BlueprintUpdatePort {
    operator fun <T : Any> invoke(
        bpId: BlueprintId,
        req: BlueprintUpdateRequest,
        responseHandler: (BlueprintUpdateRequested) -> T
    ): T
}

interface BlueprintListPort {
    operator fun <T : Any> invoke(
        query: BlueprintQuery,
        responseHandler: (List<Blueprint>) -> T
    ): T
}

interface BlueprintPort : BlueprintCreatePort, BlueprintGetPort, BlueprintUpdatePort, BlueprintListPort

@Component
class BlueprintAdapter(
    private val blueprintQueryRepository: BlueprintQueryRepository,
    private val generateDomainId: GenerateId,
    private val requestCmdRepository: RequestCmdRepository
) : BlueprintPort {
    override fun <T : Any> invoke(
        accountId: AccountId,
        req: BlueprintCreateRequest,
        responseHandler: (BlueprintCreateRequested) -> T
    ): T {
        return BlueprintCreateRequested(
            id = generateDomainId(::RequestId),
            status = Submitted,
            blueprintId = generateDomainId(::BlueprintId),
            name = req.name,
            inputs = req.inputs,
            value = req.value,
            creatorId = accountId,
            description = req.description
        ).also(requestCmdRepository::queue).let(responseHandler)
    }

    override fun <T : Any> invoke(blueprintId: BlueprintId, responseHandler: (Blueprint) -> T): T {
        return responseHandler(blueprintQueryRepository.get(blueprintId))
    }

    override fun <T : Any> invoke(
        bpId: BlueprintId,
        req: BlueprintUpdateRequest,
        responseHandler: (BlueprintUpdateRequested) -> T
    ): T {
        ensureBlueprintExists(bpId)
        return BlueprintUpdateRequested(
            id = generateDomainId(::RequestId),
            status = Submitted,
            blueprintId = bpId,
            name = req.name,
            inputs = req.inputs,
            value = req.value,
            description = req.description

            ).also(requestCmdRepository::queue).let(responseHandler)
    }

    override fun <T : Any> invoke(query: BlueprintQuery, responseHandler: (List<Blueprint>) -> T): T {
        return responseHandler(blueprintQueryRepository.list(query))
    }

    private fun ensureBlueprintExists(blueprintId: BlueprintId) = blueprintQueryRepository.get(blueprintId)
}