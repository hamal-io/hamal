package io.hamal.core.adapter

import io.hamal.lib.domain.GenerateId
import io.hamal.lib.domain._enum.RequestStatus.Submitted
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.BlueprintId
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.RequestId
import io.hamal.repository.api.Blueprint
import io.hamal.repository.api.BlueprintQueryRepository
import io.hamal.repository.api.RequestCmdRepository
import io.hamal.lib.domain.request.BlueprintCreateRequested
import io.hamal.lib.domain.request.BlueprintUpdateRequested
import io.hamal.lib.domain.request.BlueprintCreateRequest
import io.hamal.lib.domain.request.BlueprintUpdateRequest
import org.springframework.stereotype.Component

interface BlueprintCreatePort {
    operator fun <T : Any> invoke(
        groupId: GroupId,
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

interface BlueprintPort : BlueprintCreatePort, BlueprintGetPort, BlueprintUpdatePort

@Component
class BlueprintAdapter(
    private val blueprintQueryRepository: BlueprintQueryRepository,
    private val generateDomainId: GenerateId,
    private val reqCmdRepository: RequestCmdRepository
) : BlueprintPort {
    override fun <T : Any> invoke(
        groupId: GroupId,
        accountId: AccountId,
        req: BlueprintCreateRequest,
        responseHandler: (BlueprintCreateRequested) -> T
    ): T {
        return BlueprintCreateRequested(
            id = generateDomainId(::RequestId),
            status = Submitted,
            groupId = groupId,
            blueprintId = generateDomainId(::BlueprintId),
            name = req.name,
            inputs = req.inputs,
            value = req.value,
            creatorId = accountId
        ).also(reqCmdRepository::queue).let(responseHandler)
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
            groupId = blueprintQueryRepository.get(bpId).groupId,
            blueprintId = bpId,
            name = req.name,
            inputs = req.inputs,
            value = req.value,
        ).also(reqCmdRepository::queue).let(responseHandler)
    }

    private fun ensureBlueprintExists(blueprintId: BlueprintId) = blueprintQueryRepository.get(blueprintId)
}