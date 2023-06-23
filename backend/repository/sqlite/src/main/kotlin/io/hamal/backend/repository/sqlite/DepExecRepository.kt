//package io.hamal.backend.repository.sqlite
//
//import io.hamal.backend.repository.api.ExecCmdRepository
//import io.hamal.backend.repository.api.ExecQueryRepository
//import io.hamal.backend.repository.api.domain.*
//import io.hamal.backend.repository.sqlite.internal.Connection
//import io.hamal.backend.repository.sqlite.internal.Transaction
//import io.hamal.lib.common.Partition
//import io.hamal.lib.domain.CommandId
//import io.hamal.lib.domain.vo.*
//import io.hamal.lib.domain.vo.ExecStatus.Planned
//import io.hamal.lib.domain.vo.ExecStatus.Scheduled
//import java.nio.file.Path
//import java.time.Instant
//
//class SqliteExecRepository(config: Config) : BaseRepository(config), ExecCmdRepository, ExecQueryRepository {
//
//    data class Config(
//        override val path: Path,
//        override val partition: Partition
//    ) : BaseRepository.Config {
//        override val filename = "exec"
//    }
//
//
//    override fun plan(cmdId: CommandId, execToPlan: ExecCmdRepository.PlanCmd): PlannedExec {
//        return connection.tx {
//            execute(
//                """INSERT INTO execs
//                |(compute_id, account_id, id, status)
//                |VALUES
//                |(:cmdId, :accountId, :id, :status)
//                |ON CONFLICT(compute_id) DO NOTHING;""".trimMargin()
//            ) {
//                set("cmdId", cmdId)
//                set("accountId", execToPlan.accountId)
//                set("id", execToPlan.id)
//                set("status", Planned.value)
//            }
//            find(cmdId)
//        }!!.toDomainObject() as PlannedExec
//    }
//
//    override fun schedule(cmdId: CommandId, planedExec: PlannedExec): ScheduledExec {
//        return connection.tx {
//            execute(
//                """INSERT INTO execs
//                |(compute_id, account_id, id, status)
//                |VALUES
//                |(:cmdId, :accountId, :id, :status)
//                |ON CONFLICT(compute_id) DO NOTHING;""".trimMargin()
//            ) {
//                set("cmdId", cmdId)
//                set("accountId", planedExec.accountId)
//                set("id", planedExec.id)
//                set("status", Scheduled.value)
//            }
//            find(cmdId)
//        }!!.toDomainObject() as ScheduledExec
//    }
//
//    override fun enqueue(cmdId: CommandId, scheduledExec: ScheduledExec): QueuedExec {
//        TODO("Not yet implemented")
//    }
//
//    override fun complete(cmdId: CommandId, startedExec: StartedExec): CompletedExec {
//        TODO("Not yet implemented")
//    }
//
//    override fun dequeue(cmdId: CommandId): List<StartedExec> {
//        TODO("Not yet implemented")
//    }
//
//    override fun find(execId: ExecId): Exec? {
//        TODO("Not yet implemented")
//    }
//
//    override fun list(afterId: ExecId, limit: Int): List<Exec> {
//        TODO("Not yet implemented")
//    }
//
//    override fun setupConnection(connection: Connection) {
//        connection.execute("""PRAGMA journal_mode = wal;""")
//        connection.execute("""PRAGMA locking_mode = exclusive;""")
//        connection.execute("""PRAGMA temp_store = memory;""")
//        connection.execute("""PRAGMA synchronous = off;""")
//    }
//
//    override fun setupSchema(connection: Connection) {
//        connection.execute(
//            """
//            CREATE TABLE IF NOT EXISTS execs (
//                compute_id  INTEGER PRIMARY KEY,
//                account_id  INTEGER NOT NULL,
//                id          INTEGER NOT NULL,
//                status     INTEGER NOT NULL,
//                UNIQUE(id,status)
//            );
//        """.trimIndent()
//        )
//    }
//
//    override fun clear() {
//        TODO("Not yet implemented")
//    }
//}
//
//
//private fun Transaction.find(cmdId: CommandId): ExecEntity? =
//    executeQueryOne(
//        """SELECT
//                |compute_id, account_id, id, status
//                |FROM execs where compute_id = :cmdId""".trimMargin()
//    ) {
//        query {
//            set("cmdId", cmdId)
//        }
//        map { rs ->
//            ExecEntity(
//                cmdId = rs.getCommandId("compute_id"),
//                accountId = rs.getDomainId("account_id", ::AccountId),
//                id = rs.getDomainId("id", ::ExecId),
//                status = ExecStatus.valueOf(rs.getInt("status")),
//            )
//        }
//    }
//
//internal data class ExecEntity(
//    val cmdId: CommandId,
//    val accountId: AccountId,
//    val id: ExecId,
//    val status: ExecStatus,
////    val meta: ExecMeta
//) {
//    fun toDomainObject(): Exec {
//        //FIXME
//        val plannedExec = PlannedExec(
//            cmdId = cmdId,
//            accountId = accountId,
//            id = id,
//            correlation = null,
//            inputs = ExecInputs(listOf()),
//            secrets = ExecSecrets(listOf()),
//            code = Code("123"),
//            invocation = AdhocInvocation()
//        )
//
//        if (status == Planned) return plannedExec
//
//        val scheduledExec = ScheduledExec(cmdId, id, plannedExec, ScheduledAt.now())
//
//        if (status == Scheduled) return scheduledExec
//
//        TODO()
//    }
//}
//
//internal data class ExecMeta(
//    val plannedAt: Instant,
//    val scheduledAt: Instant,
//    val queuedAt: Instant,
//)