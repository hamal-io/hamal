package io.hamal.backend.repository.api.record.exec

import io.hamal.backend.repository.api.domain.AdhocInvocation
import io.hamal.backend.repository.api.domain.Exec
import io.hamal.backend.repository.api.domain.PlannedExec
import io.hamal.backend.repository.record.RecordEntity
import io.hamal.backend.repository.record.RecordSequence
import io.hamal.lib.domain.CommandId
import io.hamal.lib.domain.vo.*
import java.time.Instant

data class ExecRecordEntity(
    override val id: ExecId,
    var status: ExecStatus? = null,
    override val commandId: CommandId,
    override val sequence: RecordSequence,

    var code: Code? = null,

    var plannedAt: Instant? = null,
    var scheduledAt: Instant? = null
) : RecordEntity<ExecId> {
    fun apply(record: ExecRecord): ExecRecordEntity {
        return when (record) {
            is ExecPlannedRecord -> TODO()
            is ExecScheduledRecord -> TODO()
            else -> TODO()
        }
    }

    fun toDomainObject(): Exec {
        checkNotNull(id) { "exec id can not be null at this point" }

        val plannedExec = PlannedExec(
            commandId = CommandId(1),
            accountId = AccountId(1),
            id = id,
            correlation = null,
            inputs = ExecInputs(listOf()),
            secrets = ExecSecrets(listOf()),
            code = code!!,
            invocation = AdhocInvocation()
        )

        if (status == ExecStatus.Planned) return plannedExec

        TODO()
    }
}