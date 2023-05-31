package io.hamal.backend.service

import io.hamal.backend.repository.api.domain.CompletedExec
import io.hamal.backend.repository.api.domain.Exec
import io.hamal.backend.repository.api.domain.FailedExec
import io.hamal.backend.repository.api.domain.PlannedExec
import io.hamal.backend.service.cmd.ExecCmdService
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.ComputeId
import io.hamal.lib.domain.vo.ExecId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

@Service
class OrchestrationService
@Autowired constructor(
    internal val execCmdService: ExecCmdService
) {

    internal val lock: ReentrantLock = ReentrantLock()

    internal val backlog = mutableMapOf<Correlation, MutableList<PlannedExec>>()

    // all execs in state of scheduled or greater
    internal val execs = mutableMapOf<ExecId, Exec>()

    // mapping of correlations being inflight
    internal val inflight = mutableMapOf<Correlation, ExecId>()


    fun schedule(computeId: ComputeId, plannedExec: PlannedExec) {
        lock.withLock {
            val correlation = plannedExec.correlation

            if (correlation == null) {
                scheduleExec(computeId, plannedExec)
                return
            }

            if (!inflight.containsKey(correlation)) {
                inflight[correlation] = plannedExec.id
                scheduleExec(computeId, plannedExec)
                return
            }

            println("$correlation is in flight -> backlog")
            // correlation is inflight
            backlog.putIfAbsent(correlation, mutableListOf())
            backlog[correlation]!!.add(plannedExec)
        }
    }

    fun completed(computeId: ComputeId, completedExec: CompletedExec) {
        lock.withLock {
            if (completedExec.correlation != null) {
                // remove from inflight
                inflight.remove(completedExec.correlation)

                // if backlog - pick next
                backlog[completedExec.correlation]
                    ?.removeFirstOrNull()
                    ?.let { plannedExec ->
                        if (plannedExec.correlation != null) {
                            inflight[plannedExec.correlation!!] = plannedExec.id
                            scheduleExec(computeId, plannedExec)
                        }
                    }
            }

            execs[completedExec.id] = completedExec
        }
    }

    fun failed(computeId: ComputeId, failedExec: FailedExec) {
        lock.withLock {
            // FIXME retry or permanent fail or maybe do not fail at all...
            TODO()
        }
    }

}

fun OrchestrationService.scheduleExec(computeId: ComputeId, plannedExec: PlannedExec) {
    val scheduledExec = execCmdService.schedule(computeId, plannedExec)
    execs[scheduledExec.id] = scheduledExec
}