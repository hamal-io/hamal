package io.hamal.backend.repository.sqlite

import io.hamal.backend.repository.api.ExecCmdRepository
import io.hamal.backend.repository.api.ExecQueryRepository
import io.hamal.backend.repository.api.domain.*
import io.hamal.backend.repository.sqlite.internal.Connection
import io.hamal.backend.repository.sqlite.internal.Transaction
import io.hamal.lib.common.Shard
import io.hamal.lib.domain.CommandId
import io.hamal.lib.domain.vo.*
import io.hamal.lib.domain.vo.ExecStatus.Planned
import io.hamal.lib.domain.vo.ExecStatus.Scheduled
import java.nio.file.Path
import java.time.Instant

class SqliteExecRepository(config: Config) : BaseRepository(config), ExecCmdRepository, ExecQueryRepository {

    data class Config(
        override val path: Path,
        override val shard: Shard
    ) : BaseRepository.Config {
        override val filename = "exec"
    }


    override fun plan(commandId: CommandId, execToPlan: ExecCmdRepository.ExecToPlan): PlannedExec {
        return connection.tx {
            execute(
                """INSERT INTO execs
                |(compute_id, account_id, id, status) 
                |VALUES
                |(:commandId, :accountId, :id, :status)
                |ON CONFLICT(compute_id) DO NOTHING;""".trimMargin()
            ) {
                set("commandId", commandId)
                set("accountId", execToPlan.accountId)
                set("id", execToPlan.id)
                set("status", Planned.value)
            }
            find(commandId)
        }!!.toDomainObject() as PlannedExec
    }

    override fun schedule(commandId: CommandId, planedExec: PlannedExec): ScheduledExec {
        return connection.tx {
            execute(
                """INSERT INTO execs
                |(compute_id, account_id, id, status) 
                |VALUES
                |(:commandId, :accountId, :id, :status)
                |ON CONFLICT(compute_id) DO NOTHING;""".trimMargin()
            ) {
                set("commandId", commandId)
                set("accountId", planedExec.accountId)
                set("id", planedExec.id)
                set("status", Scheduled.value)
            }
            find(commandId)
        }!!.toDomainObject() as ScheduledExec
    }

    override fun enqueue(commandId: CommandId, scheduledExec: ScheduledExec): QueuedExec {
        TODO("Not yet implemented")
    }

    override fun complete(commandId: CommandId, inFlightExec: InFlightExec): CompletedExec {
        TODO("Not yet implemented")
    }

    override fun dequeue(commandId: CommandId): List<InFlightExec> {
        TODO("Not yet implemented")
    }

    override fun find(execId: ExecId): Exec? {
        TODO("Not yet implemented")
    }

    override fun list(afterId: ExecId, limit: Int): List<Exec> {
        TODO("Not yet implemented")
    }

    override fun setupConnection(connection: Connection) {
        connection.execute("""PRAGMA journal_mode = wal;""")
        connection.execute("""PRAGMA locking_mode = exclusive;""")
        connection.execute("""PRAGMA temp_store = memory;""")
        connection.execute("""PRAGMA synchronous = off;""")
    }

    override fun setupSchema(connection: Connection) {
        connection.execute(
            """
            CREATE TABLE IF NOT EXISTS execs (
                compute_id  INTEGER PRIMARY KEY,
                account_id  INTEGER NOT NULL,
                id          INTEGER NOT NULL,
                status     INTEGER NOT NULL,
                UNIQUE(id,status)
            );
        """.trimIndent()
        )
    }

    override fun clear() {
        TODO("Not yet implemented")
    }
}


private fun Transaction.find(commandId: CommandId): ExecEntity? =
    executeQueryOne(
        """SELECT 
                |compute_id, account_id, id, status 
                |FROM execs where compute_id = :commandId""".trimMargin()
    ) {
        query {
            set("commandId", commandId)
        }
        map { rs ->
            ExecEntity(
                commandId = rs.getCommandId("compute_id"),
                accountId = rs.getDomainId("account_id", ::AccountId),
                id = rs.getDomainId("id", ::ExecId),
                status = ExecStatus.valueOf(rs.getInt("status")),
            )
        }
    }

internal data class ExecEntity(
    val commandId: CommandId,
    val accountId: AccountId,
    val id: ExecId,
    val status: ExecStatus,
//    val meta: ExecMeta
) {
    fun toDomainObject(): Exec {
        //FIXME
        val plannedExec = PlannedExec(
            commandId = commandId,
            accountId = accountId,
            id = id,
            correlation = null,
            inputs = ExecInputs(listOf()),
            secrets = ExecSecrets(listOf()),
            code = Code("123"),
            invocation = AdhocInvocation()
        )

        if (status == Planned) return plannedExec

        val scheduledExec = ScheduledExec(commandId, id, plannedExec, ScheduledAt.now())

        if (status == Scheduled) return scheduledExec

        TODO()
    }
}

internal data class ExecMeta(
    val plannedAt: Instant,
    val scheduledAt: Instant,
    val queuedAt: Instant,
)