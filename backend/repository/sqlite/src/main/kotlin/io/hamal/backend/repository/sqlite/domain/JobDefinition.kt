package io.hamal.backend.repository.sqlite.domain

import io.hamal.backend.core.func.Func
import io.hamal.backend.repository.api.FuncRequestRepository
import io.hamal.backend.repository.api.FuncRequestRepository.Command
import io.hamal.backend.repository.api.FuncRequestRepository.Command.FuncToCreate
import io.hamal.backend.repository.sqlite.BaseRepository
import io.hamal.backend.repository.sqlite.internal.Connection
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.Shard
import io.hamal.lib.domain.vo.Code
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.FuncName
import java.nio.file.Path
import kotlin.io.path.Path


class SqliteFuncRepository(config: Config) : BaseRepository(config), FuncRequestRepository {

//    internal val lock: Lock
//    internal val connection: Connection
//    internal val template: NamedParameterJdbcTemplate


    data class Config(
        override val path: Path,
        override val shard: Shard
    ) : BaseRepository.Config {
        override val filename = "funcs"
    }

//    companion object {
//        fun open(segment: Config): DefaultJobDefinitionStore {
//            val dbPath = ensureDirectoryExists(segment)
//
//            val dataSource = DriverManagerDataSource()
//            dataSource.setDriverClassName("org.sqlite.JDBC")
//            dataSource.setUrl("jdbc:sqlite:$dbPath.db")
//
//            val template = NamedParameterJdbcTemplate(dataSource)
//
//            val result = DefaultJobDefinitionStore(
//                lock = ReentrantLock(),
////                connection = DriverManager.getConnection("jdbc:sqlite:$dbPath.db"),
//                connection = template.jdbcTemplate.dataSource.connection,
//                template = template
//            )
//
//            result.setupSqlite()
//            result.setupSchema()
//
//            return result
//        }
//
//        private fun ensureDirectoryExists(config: Config): Path {
//            return Files.createDirectories(config.path)
//                .resolve(Path(String.format("job-definitions-%04d", config.shard.value.toLong())))
//        }
//    }

    override fun get(id: FuncId): Func {
        return connection.executeQueryOne("SELECT id, version, request_id, reference FROM funcs WHERE id = :id") {
            with { set("id", id) }
            map {
                Func(
                    id = it.getDomainId("id", ::FuncId),
                    name = FuncName(it.getString("reference")),
                    code = Code("")
                )
            }
        } ?: throw IllegalArgumentException("No job definition found for $id")
    }


    override fun execute(reqId: ReqId, commands: List<Command>): List<Func> {

        val toProcess = commands.groupBy { it.funcId }

        //            template.update(
//                "INSERT INTO request_log(id, instant) VALUES (:id, unixepoch())",
//                mapOf("id" to BigDecimal("123"))
//            )

//        this.apply { }

//        inTx { operations ->
//            operations.execute("INSERT INTO request_log(id, instant) VALUES (:id, unixepoch())") {
//                set("id", requestId)
//            }
//            operations.abort()
//        }


//        inTx {
////            execute("INSERT INTO request_log(id, instant) VALUES (:id, unixepoch())") {
////                set("id", requestId)
////            }
//        }

//        val operations = Operations()
//        operations.execute("""select *""") {
//            set("popo", "smells")
//        }
//
//        operations.execute("PRAGMA journal_mode = exclusive;")

//    template.execute("PRAGMA temp_store = memory") {}

//
//        try {
//            connection.autoCommit = false
//            connection.beginRequest()
//
//            toProcess.forEach { (id, commands) ->
//                commands.sortedBy { it.order }.forEach { command ->
//                    when (command) {
//                        is JobDefinitionToInsert -> insertJobDefinition(requestId, command)
//                        else -> TODO("Command not supported yet $command")
//                    }
//                }
//                updateVersion(id)
//            }
//
//            connection.commit()
//        } catch (t: Throwable) {
//            connection.rollback()
//            throw t
//        } finally {
//            connection.endRequest()
//            connection.autoCommit = true
//        }
//
        return listOf(
            Func(
                id = toProcess.keys.first(),
                name = FuncName("das"),
                Code("")
            )
        )
    }

//    override fun setupConnection(operations: NamedParameterJdbcOperations) {
//    override fun setupConnection(connection: Connection) {
////        NamedParameters {
////            set("abc", 123)
////        }
////        TODO("Not yet implemented")
//    }
//
////    override fun setupSchema(operations: NamedParameterJdbcOperations) {
//    override fun setupSchema(connection: Connection) {
////        inTx {
////            execute(
////                """
////            CREATE TABLE IF NOT EXISTS request_log (
////                id          BIGINT PRIMARY KEY,
////                instant     DATETIME NOT NULL
////            );
////            """.trimIndent()
////            )
////
////            execute(
////                """
////            CREATE TABLE IF NOT EXISTS funcs (
////                id          INTEGER PRIMARY KEY,
////                version     INTEGER NOT NULL ,
////                reference   TEXT NOT NULL ,
////                inputs      BLOB,
////                secrets     BLOB,
////                instant     DATETIME NOT NULL
////            );
////            """.trimIndent()
////            )
////
////            execute(
////                """
////           CREATE TABLE IF NOT EXISTS triggers(
////                id INTEGER PRIMARY KEY,
////                func_id INTEGER NOT NULL,
////                type INTEGER NOT NULL,
////                inputs BLOB,
////                secrets BLOB,
////                data BLOB
////            );
////            """.trimIndent()
////            )
////        }
//    }

//    override fun drop() {
////        inTx {
////            execute("""DROP TABLE IF EXISTS funcs;""")
////            execute("""DROP TABLE IF EXISTS triggers;""")
////            execute("""DROP TABLE IF EXISTS request_log;""")
////        }
//    }

//    override fun setupConnection() {
//        connection.execute("""PRAGMA journal_mode = wal;""")
//        connection.execute("""PRAGMA locking_mode = exclusive;""")
//        connection.execute("""PRAGMA temp_store = memory;""")
//        connection.execute("""PRAGMA synchronous = off;""")
//    }
//
//    override fun setupSchema() {
//
//        connection.execute("""DROP TABLE IF EXISTS funcs;""")
//        connection.execute("""DROP TABLE IF EXISTS triggers;""")
//        connection.execute(
//            """
//            CREATE TABLE IF NOT EXISTS funcs (
//                id          INTEGER PRIMARY KEY,
//                version     INTEGER NOT NULL ,
//                request_id  BIGINT  NOT NULL,
//                reference   TEXT NOT NULL ,
//                inputs      BLOB,
//                secrets     BLOB,
//                instant     DATETIME NOT NULL,
//                UNIQUE (request_id)
//            );
//        """.trimIndent()
//        )
//
//        connection.execute(
//            """
//           CREATE TABLE IF NOT EXISTS triggers(
//                id INTEGER PRIMARY KEY,
//                func_id INTEGER NOT NULL,
//                type INTEGER NOT NULL,
//                inputs BLOB,
//                secrets BLOB,
//                data BLOB
//            );""".trimIndent()
//        )
//    }

    override fun setupConnection(connection: Connection) {
        TODO("Not yet implemented")
    }

    override fun setupSchema(connection: Connection) {
        TODO("Not yet implemented")
    }

    override fun clear() {
        TODO("Not yet implemented")
    }

}

internal fun SqliteFuncRepository.insertFunc(reqId: ReqId, toInsert: FuncToCreate) {
//    return connection.prepareStatement(
//        """INSERT INTO funcs(id, version, request_id,reference, instant) VALUES(?,?,?,?,?)""",
//    ).use {
//        it.setLong(1, toInsert.jobDefinitionId.value.value)
//        it.setInt(2, 0)
//        it.setBigDecimal(3, requestId.value.toBigDecimal())
//        it.setString(4, toInsert.reference.value.value)
//        it.setTimestamp(5, Timestamp.from(TimeUtils.now()))
//        it.execute()
//    }
}

internal fun SqliteFuncRepository.updateVersion(id: FuncId) {
//    return connection.prepareStatement(
//        """UPDATE funcs SET version = version + 1 WHERE id = ?""",
//    ).use {
//        it.setLong(1, id.value.value)
//        it.execute()
//    }
}

fun main() {
    val store = SqliteFuncRepository(
        SqliteFuncRepository.Config(
            path = Path("/tmp/db0815"),
            shard = Shard(0)
        )
    )


//    val dataSource = DriverManagerDataSource()
//    dataSource.setDriverClassName("org.sqlite.JDBC")
//    dataSource.setUrl("jdbc:sqlite:/tmp/test.sqlite")
//
//    val transactionManager = DataSourceTransactionManager(dataSource);
//    val transactionTemplate = TransactionTemplate(transactionManager);
//    transactionTemplate.propagationBehavior = TransactionDefinition.PROPAGATION_REQUIRES_NEW;
//
//    val template = NamedParameterJdbcTemplate(dataSource)
//
//
//    val id = JobDefinitionId(SnowflakeId(2199023255552))
//
////    transactionTemplate.execute {
//    template.execute("PRAGMA journal_mode = wal;") {}
//    template.execute("PRAGMA journal_mode = exclusive;") {}
//    template.execute("PRAGMA temp_store = memory") {}
//    template.execute("PRAGMA synchronous = off") {}
////    }
//
//    transactionTemplate.execute {
//
//        template.jdbcTemplate.execute("""DROP TABLE IF EXISTS funcs;""")
//        template.jdbcTemplate.execute("""DROP TABLE IF EXISTS triggers;""")
//        template.update("""DROP TABLE IF EXISTS request_log;""", mapOf<String, Any>())
//        template.update(
//            """
//            CREATE TABLE IF NOT EXISTS request_log (
//                id          BIGINT PRIMARY KEY,
//                instant     DATETIME NOT NULL
//            );
//        """.trimIndent(), mapOf<String, Any>()
//        )
//        template.jdbcTemplate.execute(
//            """
//            CREATE TABLE IF NOT EXISTS funcs (
//                id          INTEGER PRIMARY KEY,
//                version     INTEGER NOT NULL ,
//                reference   TEXT NOT NULL ,
//                inputs      BLOB,
//                secrets     BLOB,
//                instant     DATETIME NOT NULL
//            );
//        """.trimIndent()
//        )
//        template.jdbcTemplate.execute(
//            """
//           CREATE TABLE IF NOT EXISTS triggers(
//                id INTEGER PRIMARY KEY,
//                func_id INTEGER NOT NULL,
//                type INTEGER NOT NULL,
//                inputs BLOB,
//                secrets BLOB,
//                data BLOB
//            );""".trimIndent()
//        )
//    }
//
//
//    try {
//        val r = transactionTemplate.execute {
//
//            template.update(
//                "INSERT INTO request_log(id, instant) VALUES (:id, unixepoch())",
//                mapOf("id" to BigDecimal("123"))
//            )
//
//            template.query(
//                """INSERT INTO funcs(id, version, reference, instant)
//                    |    VALUES (:id,:version,:reference,:instant)
//                    |    RETURNING *""".trimMargin(),
//                mapOf(
//                    "id" to id.value.value,
//                    "version" to 0,
//                    "reference" to "abc",
//                    "instant" to Timestamp.from(Instant.now())
//                )
//            ) {
//                println(it.getLong("id"))
//                println(it.getString("reference"))
//            }
//
//
//            template.query(
//                """SELECT id, version, reference FROM funcs WHERE id = :id""",
//                mapOf("id" to id.value.value)
//            ) { resultSet, idx ->
//                println(resultSet.fetchSize)
//                JobDefinition(
//                    id = JobDefinitionId(SnowflakeId(resultSet.getLong("id"))),
//                    reference = JobReference(resultSet.getString("reference")),
//                    triggers = listOf()
//                )
//            }
//        }
//        println(r)
//
//    } catch (t: Throwable) {
//        // thre request was already handled - get job definition and proceed with processing
//        if (t.localizedMessage.contains("UNIQUE constraint failed: request_log.id")) {
//            return
//        }
//        println(t.localizedMessage)
//    }
}