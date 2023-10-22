package io.hamal.repository.sqlite.record.snippet

import io.hamal.lib.domain.vo.SnippetId
import io.hamal.lib.sqlite.SqliteBaseRepository
import io.hamal.repository.api.Snippet
import io.hamal.repository.api.SnippetCmdRepository.CreateCmd
import io.hamal.repository.api.SnippetCmdRepository.UpdateCmd
import io.hamal.repository.api.SnippetQueryRepository.SnippetQuery
import io.hamal.repository.api.SnippetRepository
import io.hamal.repository.record.CreateDomainObject
import io.hamal.repository.record.snippet.SnippetCreationRecord
import io.hamal.repository.record.snippet.SnippetEntity
import io.hamal.repository.record.snippet.SnippetRecord
import io.hamal.repository.record.snippet.SnippetUpdatedRecord
import io.hamal.repository.sqlite.record.SqliteRecordRepository
import java.nio.file.Path

internal object CreateSnippet : CreateDomainObject<SnippetId, SnippetRecord, Snippet> {
    override fun invoke(recs: List<SnippetRecord>): Snippet {
        check(recs.isNotEmpty()) { "At least one record is required" }
        val firstRecord = recs.first()
        check(firstRecord is SnippetCreationRecord)

        var result = SnippetEntity(
            cmdId = firstRecord.cmdId,
            id = firstRecord.entityId,
            groupId = firstRecord.groupId,
            creatorId = firstRecord.creatorId,
            sequence = firstRecord.sequence()
        )

        recs.forEach { record ->
            result = result.apply(record)
        }

        return result.toDomainObject()
    }
}

class SqliteSnippetRepository(
    config: Config
) : SqliteRecordRepository<SnippetId, SnippetRecord, Snippet>(
    config = config,
    createDomainObject = CreateSnippet,
    recordClass = SnippetRecord::class,
    projections = listOf(ProjectionCurrent)
), SnippetRepository {
    data class Config(
        override val path: Path
    ) : SqliteBaseRepository.Config {
        override val filename = "snippet.db"
    }

    override fun create(cmd: CreateCmd): Snippet {
        val snippetId = cmd.snippetId
        val cmdId = cmd.id
        return tx {
            if (commandAlreadyApplied(cmdId, snippetId)) {
                versionOf(snippetId, cmdId)
            } else {
                store(
                    SnippetCreationRecord(
                        cmdId = cmdId,
                        entityId = snippetId,
                        groupId = cmd.groupId,
                        name = cmd.name,
                        inputs = cmd.inputs,
                        value = cmd.value,
                        creatorId = cmd.creatorId
                    )
                )

                currentVersion(snippetId)
                    .also { ProjectionCurrent.upsert(this, it) }
            }
        }
    }

    override fun update(snippetId: SnippetId, cmd: UpdateCmd): Snippet {
        val cmdId = cmd.id
        return tx {
            if (commandAlreadyApplied(cmdId, snippetId)) {
                versionOf(snippetId, cmdId)
            } else {
                val currentVersion = versionOf(snippetId, cmdId)
                store(
                    SnippetUpdatedRecord(
                        entityId = snippetId,
                        cmdId = cmdId,
                        name = cmd.name ?: currentVersion.name,
                        inputs = cmd.inputs ?: currentVersion.inputs,
                        value = cmd.value ?: currentVersion.value
                    )
                )
                currentVersion(snippetId)
                    .also { ProjectionCurrent.upsert(this, it) }
            }
        }
    }

    override fun find(snippetId: SnippetId): Snippet? {
        return ProjectionCurrent.find(connection, snippetId)

    }

    override fun list(query: SnippetQuery): List<Snippet> {
        return ProjectionCurrent.list(connection, query)
    }

    override fun count(query: SnippetQuery): ULong {
        return ProjectionCurrent.count(connection, query)
    }
}