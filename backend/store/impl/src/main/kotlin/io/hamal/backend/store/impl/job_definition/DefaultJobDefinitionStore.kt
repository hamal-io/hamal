package io.hamal.backend.store.impl.job_definition

import io.hamal.backend.core.model.JobDefinition
import io.hamal.backend.store.api.JobDefinitionStore
import io.hamal.backend.store.api.JobDefinitionStore.Command
import io.hamal.backend.store.api.JobDefinitionStore.Command.JobDefinitionToInsert
import io.hamal.backend.store.impl.BaseStore
import io.hamal.lib.RequestId
import io.hamal.lib.Shard
import io.hamal.lib.vo.JobDefinitionId
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations
import java.nio.file.Path
import kotlin.io.path.Path


class DefaultJobDefinitionStore(config: Config) : BaseStore(config), JobDefinitionStore {

//    internal val lock: Lock
//    internal val connection: Connection
//    internal val template: NamedParameterJdbcTemplate


    data class Config(
        override val path: Path,
        override val shard: Shard
    ) : BaseStore.Config {
        override val name = "job_definitions"
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

    override fun get(id: JobDefinitionId): JobDefinition {
        TODO()
//        return operations.queryForObject(
//            """SELECT id, version, request_id, reference FROM job_definitions WHERE id = :id""",
//            mapOf("id" to id.value)
//        ) { resultSet, idx ->
//            JobDefinition(
//                id = JobDefinitionId(SnowflakeId(resultSet.getLong("id"))),
//                reference = JobReference(resultSet.getString("reference")),
//                triggers = listOf()
//            )
//        }!!
    }

    override fun execute(requestId: RequestId, commands: List<Command>): List<JobDefinition> {
//        val toProcess = commands.groupBy { it.jobDefinitionId }

        //            template.update(
//                "INSERT INTO request_log(id, instant) VALUES (:id, unixepoch())",
//                mapOf("id" to BigDecimal("123"))
//            )

        this.apply { }

//        inTx { operations ->
//            operations.execute("INSERT INTO request_log(id, instant) VALUES (:id, unixepoch())") {
//                set("id", requestId)
//            }
//            operations.abort()
//        }


        inTx {
            execute("INSERT INTO request_log(id, instant) VALUES (:id, unixepoch())") {
                set("id", requestId)
            }
        }

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
//                        is ManualTriggerToInsert -> println("Insert Trigger: $command")
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
//        return listOf(
//            JobDefinition(
//                id = toProcess.keys.first(),
//                reference = JobReference("das"),
//                triggers = listOf()
//            )
//        )
        TODO()
    }

    override fun setupConnection(operations: NamedParameterJdbcOperations) {
//        NamedParameters {
//            set("abc", 123)
//        }
//        TODO("Not yet implemented")
    }

    override fun setupSchema(operations: NamedParameterJdbcOperations) {
        inTx {
            execute(
                """
            CREATE TABLE IF NOT EXISTS request_log (
                id          BIGINT PRIMARY KEY,
                instant     DATETIME NOT NULL
            );
            """.trimIndent()
            )

            execute(
                """
            CREATE TABLE IF NOT EXISTS job_definitions (
                id          INTEGER PRIMARY KEY,
                version     INTEGER NOT NULL ,
                reference   TEXT NOT NULL ,
                inputs      BLOB,
                secrets     BLOB,
                instant     DATETIME NOT NULL
            );
            """.trimIndent()
            )

            execute(
                """
           CREATE TABLE IF NOT EXISTS triggers(
                id INTEGER PRIMARY KEY,
                job_definition_id INTEGER NOT NULL,
                type INTEGER NOT NULL,
                inputs BLOB,
                secrets BLOB,
                data BLOB
            );
            """.trimIndent()
            )
        }
    }

    override fun drop() {
        inTx {
            execute("""DROP TABLE IF EXISTS job_definitions;""")
            execute("""DROP TABLE IF EXISTS triggers;""")
            execute("""DROP TABLE IF EXISTS request_log;""")
        }
    }

}

internal fun DefaultJobDefinitionStore.insertJobDefinition(requestId: RequestId, toInsert: JobDefinitionToInsert) {
//    return connection.prepareStatement(
//        """INSERT INTO job_definitions(id, version, request_id,reference, instant) VALUES(?,?,?,?,?)""",
//    ).use {
//        it.setLong(1, toInsert.jobDefinitionId.value.value)
//        it.setInt(2, 0)
//        it.setBigDecimal(3, requestId.value.toBigDecimal())
//        it.setString(4, toInsert.reference.value.value)
//        it.setTimestamp(5, Timestamp.from(TimeUtils.now()))
//        it.execute()
//    }
}

internal fun DefaultJobDefinitionStore.updateVersion(id: JobDefinitionId) {
//    return connection.prepareStatement(
//        """UPDATE job_definitions SET version = version + 1 WHERE id = ?""",
//    ).use {
//        it.setLong(1, id.value.value)
//        it.execute()
//    }
}

fun main() {
    val store = DefaultJobDefinitionStore(
        DefaultJobDefinitionStore.Config(
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
//        template.jdbcTemplate.execute("""DROP TABLE IF EXISTS job_definitions;""")
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
//            CREATE TABLE IF NOT EXISTS job_definitions (
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
//                job_definition_id INTEGER NOT NULL,
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
//                """INSERT INTO job_definitions(id, version, reference, instant)
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
//                """SELECT id, version, reference FROM job_definitions WHERE id = :id""",
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