package io.hamal.backend.service

import io.hamal.backend.repository.api.domain.CompletedExec
import io.hamal.backend.repository.api.domain.FailedExec
import io.hamal.backend.repository.api.domain.PlannedExec
import io.hamal.backend.service.cmd.ExecCmdService
import io.hamal.lib.domain.ReqId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class OrchestrationService
@Autowired constructor(val execCmdService: ExecCmdService) {

    fun schedule(reqId: ReqId, plannedExec: PlannedExec) {
        // if correlation is active --> add to backlog
        // otherwise --> schedule
        execCmdService.schedule(
            ExecCmdService.ToSchedule(
                reqId = reqId,
                plannedExec = plannedExec
            )
        )
    }

    fun completed(reqId: ReqId, completedExec: CompletedExec) {

    }

    fun failed(reqId: ReqId, failedExec: FailedExec) {

    }

}