package io.hamal.backend.infra.usecase.request.exec

import io.hamal.backend.core.exec.PlannedExec
import io.hamal.backend.core.logger
import io.hamal.backend.core.notification.ExecPlannedNotification
import io.hamal.backend.core.notification.port.NotifyDomainPort
import io.hamal.backend.infra.usecase.request.ExecRequest.PlanExec
import io.hamal.backend.repository.api.ExecRequestRepository
import io.hamal.lib.domain.ddd.RequestOneUseCaseHandler
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.port.GenerateDomainIdPort

class PlanExecRequestHandler(
    val generateDomainId: GenerateDomainIdPort,
    val execRequestRepository: ExecRequestRepository,
    val notifyDomain: NotifyDomainPort
) : RequestOneUseCaseHandler<PlannedExec, PlanExec>(PlanExec::class) {
    val log = logger(PlanExecRequestHandler::class)
    override fun invoke(useCase: PlanExec): PlannedExec {
        log.debug("Create a new exec for ${useCase.code}")

        val result = execRequestRepository.plan(
            useCase.reqId,
            ExecRequestRepository.ExecToPlan(
                shard = useCase.shard,
                id = generateDomainId(useCase.shard, ::ExecId),
                code = useCase.code,
                trigger = useCase.trigger
            )
        )

        notifyDomain(
            ExecPlannedNotification(
                shard = result.shard,
                plannedExec = result
            )
        )

        return result
    }

}