package io.hamal.backend.instance.service

import io.hamal.backend.instance.service.cmd.ExecCmdService
import io.hamal.backend.repository.api.domain.CompletedExec
import io.hamal.backend.repository.api.domain.Exec
import io.hamal.backend.repository.api.domain.FailedExec
import io.hamal.backend.repository.api.domain.PlannedExec
import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.Correlation
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


    fun schedule(cmdId: CmdId, plannedExec: PlannedExec) {
        lock.withLock {
            val correlation = plannedExec.correlation

            if (correlation == null) {
                scheduleExec(cmdId, plannedExec)
                return
            }

            if (!inflight.containsKey(correlation)) {
                inflight[correlation] = plannedExec.id
                scheduleExec(cmdId, plannedExec)
                return
            }

            println("$correlation is in flight -> backlog")
            // correlation is inflight
            backlog.putIfAbsent(correlation, mutableListOf())
            backlog[correlation]!!.add(plannedExec)
        }
    }

    fun completed(cmdId: CmdId, completedExec: CompletedExec) {
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
                            scheduleExec(cmdId, plannedExec)
                        }
                    }
            }

            execs[completedExec.id] = completedExec
        }
    }

    fun failed(cmdId: CmdId, failedExec: FailedExec) {
        lock.withLock {
            // FIXME retry or permanent fail or maybe do not fail at all...
            TODO()
        }
    }

}

fun OrchestrationService.scheduleExec(cmdId: CmdId, plannedExec: PlannedExec) {
    val scheduledExec = execCmdService.schedule(cmdId, plannedExec)
    execs[scheduledExec.id] = scheduledExec
}