package io.hamal.backend.store.impl.job_definition

import io.hamal.backend.core.model.JobDefinition
import io.hamal.backend.store.api.JobDefinitionStore
import io.hamal.backend.store.api.JobDefinitionStore.Command.JobDefinitionToInsert
import io.hamal.backend.store.api.JobDefinitionStore.Command.ManualTriggerToInsert
import io.hamal.backend.store.api.insertJobDefinition
import io.hamal.backend.store.api.insertManualTrigger
import io.hamal.lib.RequestId
import io.hamal.lib.Shard
import io.hamal.lib.util.Files
import io.hamal.lib.vo.JobDefinitionId
import io.hamal.lib.vo.JobReference
import java.nio.file.Path
import java.sql.Connection
import java.sql.DriverManager
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import kotlin.io.path.Path


class DefaultJobDefinitionStore private constructor(
    internal val lock: Lock,
    internal val connection: Connection
) : JobDefinitionStore(), AutoCloseable {
    data class Config(
        val path: Path,
        val shard: Shard
    )

    companion object {
        fun open(segment: Config): DefaultJobDefinitionStore {
            val dbPath = ensureDirectoryExists(segment)
            val result = DefaultJobDefinitionStore(
                lock = ReentrantLock(),
                connection = DriverManager.getConnection("jdbc:sqlite:$dbPath.db")
            )

            result.setupSqlite()
            result.setupSchema()

            return result
        }

        private fun ensureDirectoryExists(config: Config): Path {
            return Files.createDirectories(config.path)
                .resolve(Path(String.format("job-definitions-%04d", config.shard.value.toLong())))
        }
    }

    override fun execute(requestId: RequestId, commands: List<Command>): List<JobDefinition> {
        val toProcess = commands.groupBy { it.jobDefinitionId }

        connection.beginRequest()
        // start transaction
        toProcess.forEach { (_, commands) ->
            commands.sortedBy { it.order }.forEach { command ->
                when (command) {
                    is JobDefinitionToInsert -> println("Insert Definition: $command")
                    is ManualTriggerToInsert -> println("Insert Trigger: $command")
                    else -> TODO("Command not supported yet $command")
                }
            }
        }
        // stop transaction
        connection.endRequest()

        // select toProccess.keys

        return listOf(
            JobDefinition(
                id = JobDefinitionId(10),
                reference = JobReference("das"),
                triggers = listOf()
            )
        )
    }

    override fun close() {
        connection.close()
    }
}



internal fun DefaultJobDefinitionStore.updateVersion(id: JobDefinitionId) {

}

fun main() {

    val store = DefaultJobDefinitionStore.open(
        DefaultJobDefinitionStore.Config(
            path = Path("/tmp/hamal"),
            shard = Shard(0)
        )
    )

    val jobDefinition = store.request(RequestId(1)) {
        val jobDefinitionId = it.insertJobDefinition {
            this.reference = JobReference("abc")
        }

        it.insertManualTrigger(jobDefinitionId) {}
        it.insertManualTrigger(jobDefinitionId) {}
    }


    println(jobDefinition)
}

