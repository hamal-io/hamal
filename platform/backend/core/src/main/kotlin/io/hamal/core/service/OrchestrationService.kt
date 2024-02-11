package io.hamal.core.service

import io.hamal.core.event.InternalEventEmitter
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.vo.ExecId
import io.hamal.repository.api.Exec
import io.hamal.repository.api.ExecCmdRepository
import io.hamal.repository.api.ExecCmdRepository.ScheduleCmd
import io.hamal.repository.api.event.ExecScheduledEvent
import org.springframework.stereotype.Service
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

@Service
internal class OrchestrationService(
    private val execCmdRepository: ExecCmdRepository,
    private val eventEmitter: InternalEventEmitter
) {

    internal val lock: ReentrantLock = ReentrantLock()
    internal val backlog = mutableMapOf<Correlation, MutableList<Exec.Planned>>()

    // all execs in state of scheduled or greater
    internal val execs = mutableMapOf<ExecId, Exec>()

    // mapping of correlations being inflight
    internal val inflight = mutableMapOf<Correlation, ExecId>()


    fun schedule(cmdId: CmdId, plannedExec: Exec.Planned) {
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

    fun completed(cmdId: CmdId, completedExec: Exec.Completed) {
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

    fun failed(cmdId: CmdId, failedExec: Exec.Failed) {
        lock.withLock {
            // FIXME retry or permanent fail or maybe do not fail at all...
            // remove from inflight
            if (failedExec.correlation != null) {
                inflight.remove(failedExec.correlation)

                // if backlog - pick next
                backlog[failedExec.correlation]
                    ?.removeFirstOrNull()
                    ?.let { plannedExec ->
                        if (plannedExec.correlation != null) {
                            inflight[plannedExec.correlation!!] = plannedExec.id
                            scheduleExec(cmdId, plannedExec)
                        }
                    }
            }

            execs[failedExec.id] = failedExec
        }
    }

    private fun scheduleExec(cmdId: CmdId, plannedExec: Exec.Planned) {
        val scheduledExec =
            execCmdRepository.schedule(ScheduleCmd(cmdId, plannedExec.id))
                .also { emitEvent(cmdId, it) }

        execs[scheduledExec.id] = scheduledExec
    }

    private fun emitEvent(cmdId: CmdId, exec: Exec.Scheduled) {
        eventEmitter.emit(cmdId, ExecScheduledEvent(exec))
    }
}

