package io.hamal.repository.record.exec

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.vo.*
import io.hamal.repository.record.Record
import io.hamal.repository.record.RecordAdapter
import io.hamal.repository.record.RecordSequence
import io.hamal.repository.record.RecordedAt

sealed class ExecRecord(
    @Transient
    override var recordSequence: RecordSequence? = null,
    @Transient
    override var recordedAt: RecordedAt? = null
) : Record<ExecId>() {
    internal object Adapter : RecordAdapter<ExecRecord>(
        listOf(
            Planned::class,
            Scheduled::class,
            Queued::class,
            Started::class,
            Completed::class,
            Failed::class,
        )
    )

    data class Planned(
        override val cmdId: CmdId,
        override val entityId: ExecId,
        val triggerId: TriggerId?,
        val namespaceId: NamespaceId,
        val workspaceId: WorkspaceId,
        val correlation: Correlation?,
        val inputs: ExecInputs,
        val code: ExecCode,
        val invocation: Invocation
    ) : ExecRecord()

    data class Scheduled(
        override val cmdId: CmdId,
        override val entityId: ExecId,
    ) : ExecRecord()

    data class Queued(
        override val cmdId: CmdId,
        override val entityId: ExecId,
    ) : ExecRecord()


    data class Started(
        override val cmdId: CmdId,
        override val entityId: ExecId,
    ) : ExecRecord()

    data class Completed(
        override val cmdId: CmdId,
        override val entityId: ExecId,
        val result: ExecResult,
        val state: ExecState
    ) : ExecRecord()

    data class Failed(
        override val cmdId: CmdId,
        override val entityId: ExecId,
        val result: ExecResult
    ) : ExecRecord()
}

