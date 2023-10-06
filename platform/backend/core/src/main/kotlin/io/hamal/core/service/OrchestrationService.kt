package io.hamal.core.service

import io.hamal.core.event.PlatformEventEmitter
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.vo.ExecId
import io.hamal.repository.api.event.ExecScheduledEvent
import org.springframework.stereotype.Service
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

@Service
internal class OrchestrationService(
    private val execCmdRepository: io.hamal.repository.api.ExecCmdRepository,
    private val eventEmitter: PlatformEventEmitter
) {

    internal val lock: ReentrantLock = ReentrantLock()
    internal val backlog = mutableMapOf<Correlation, MutableList<io.hamal.repository.api.PlannedExec>>()

    // all execs in state of scheduled or greater
    internal val execs = mutableMapOf<ExecId, io.hamal.repository.api.Exec>()

    // mapping of correlations being inflight
    internal val inflight = mutableMapOf<Correlation, ExecId>()


    fun schedule(cmdId: CmdId, plannedExec: io.hamal.repository.api.PlannedExec) {
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

    fun completed(cmdId: CmdId, completedExec: io.hamal.repository.api.CompletedExec) {
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

    fun failed(cmdId: CmdId, failedExec: io.hamal.repository.api.FailedExec) {
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

    fun scheduleExec(cmdId: CmdId, plannedExec: io.hamal.repository.api.PlannedExec) {
        val scheduledExec =
            execCmdRepository.schedule(io.hamal.repository.api.ExecCmdRepository.ScheduleCmd(cmdId, plannedExec.id))
                .also { emitEvent(cmdId, it) }

        execs[scheduledExec.id] = scheduledExec
    }

    fun emitEvent(cmdId: CmdId, exec: io.hamal.repository.api.ScheduledExec) {
        eventEmitter.emit(cmdId, ExecScheduledEvent(exec))
    }
}

