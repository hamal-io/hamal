package io.hamal.core.adapter

import io.hamal.core.req.SubmitRequest
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.BlueprintId
import io.hamal.lib.domain.vo.GroupId
import io.hamal.repository.api.Blueprint
import io.hamal.repository.api.BlueprintQueryRepository
import io.hamal.repository.api.submitted_req.BlueprintCreateSubmitted
import io.hamal.repository.api.submitted_req.BlueprintUpdateSubmitted
import io.hamal.request.CreateBlueprintReq
import io.hamal.request.UpdateBlueprintReq
import org.springframework.stereotype.Component

interface BlueprintCreatePort {
    operator fun <T : Any> invoke(
        groupId: GroupId,
        accountId: AccountId,
        req: CreateBlueprintReq,
        responseHandler: (BlueprintCreateSubmitted) -> T
    ): T
}

interface BlueprintGetPort {
    operator fun <T : Any> invoke(blueprintId: BlueprintId, responseHandler: (Blueprint) -> T): T
}


interface BlueprintUpdatePort {
    operator fun <T : Any> invoke(
        blueprintId: BlueprintId,
        req: UpdateBlueprintReq,
        responseHandler: (BlueprintUpdateSubmitted) -> T
    ): T
}

interface BlueprintPort : BlueprintCreatePort, BlueprintGetPort, BlueprintUpdatePort

@Component
class BlueprintAdapter(
    private val submitRequest: SubmitRequest,
    private val blueprintQueryRepository: BlueprintQueryRepository,
) : BlueprintPort {
    override fun <T : Any> invoke(
        groupId: GroupId,
        accountId: AccountId,
        req: CreateBlueprintReq,
        responseHandler: (BlueprintCreateSubmitted) -> T
    ): T {
        return responseHandler(submitRequest(groupId, accountId, req))
    }

    override fun <T : Any> invoke(blueprintId: BlueprintId, responseHandler: (Blueprint) -> T): T {
        return responseHandler(blueprintQueryRepository.get(blueprintId))
    }

    override fun <T : Any> invoke(
        blueprintId: BlueprintId,
        req: UpdateBlueprintReq,
        responseHandler: (BlueprintUpdateSubmitted) -> T
    ): T {
        ensureBlueprintExists(blueprintId)
        return responseHandler(submitRequest(blueprintId, req))
    }

    private fun ensureBlueprintExists(blueprintId: BlueprintId) = blueprintQueryRepository.get(blueprintId)
}