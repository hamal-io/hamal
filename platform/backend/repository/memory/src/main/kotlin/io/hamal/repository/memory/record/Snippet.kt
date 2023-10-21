package io.hamal.repository.memory.record

import io.hamal.lib.domain.vo.SnippetId
import io.hamal.repository.api.Snippet
import io.hamal.repository.api.SnippetCmdRepository.CreateCmd
import io.hamal.repository.api.SnippetCmdRepository.UpdateCmd
import io.hamal.repository.api.SnippetQueryRepository.SnippetQuery
import io.hamal.repository.api.SnippetRepository
import io.hamal.repository.record.snippet.CreateSnippetFromRecords
import io.hamal.repository.record.snippet.SnippetCreationRecord
import io.hamal.repository.record.snippet.SnippetRecord
import io.hamal.repository.record.snippet.SnippetUpdatedRecord
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

internal object CurrentSnippetProjection {
    private val projection = mutableMapOf<SnippetId, Snippet>()
    fun apply(snippet: Snippet) {
        projection[snippet.id] = snippet
    }

    fun find(snippetId: SnippetId): Snippet? = projection[snippetId]

    fun list(query: SnippetQuery): List<Snippet> {
        return projection.filter { query.snippetIds.isEmpty() || it.key in query.snippetIds }
            .map { it.value }
            .reversed()
            .asSequence()
            .filter { if (query.groupIds.isEmpty()) true else query.groupIds.contains(it.groupId) }
            .dropWhile { it.id >= query.afterId }
            .take(query.limit.value)
            .toList()
    }

    fun count(query: SnippetQuery): ULong {
        return projection.filter { query.snippetIds.isEmpty() || it.key in query.snippetIds }
            .map { it.value }
            .reversed()
            .asSequence()
            .filter { if (query.groupIds.isEmpty()) true else query.groupIds.contains(it.groupId) }
            .dropWhile { it.id >= query.afterId }
            .count()
            .toULong()
    }

    fun clear() {
        projection.clear()
    }
}

class MemorySnippetRepository : MemoryRecordRepository<SnippetId, SnippetRecord, Snippet>(
    createDomainObject = CreateSnippetFromRecords,
    recordClass = SnippetRecord::class
), SnippetRepository {
    private val lock = ReentrantLock()

    override fun create(cmd: CreateCmd): Snippet {
        return lock.withLock {
            val snippedId = cmd.snippetId
            val cmdId = cmd.id
            if (commandAlreadyApplied(cmdId, snippedId)) {
                versionOf(snippedId, cmd.id)
            } else {
                store(
                    SnippetCreationRecord(
                        cmdId = cmd.id,
                        entityId = snippedId,
                        groupId = cmd.groupId,
                        name = cmd.name,
                        inputs = cmd.inputs,
                        value = cmd.value,
                        creatorId = cmd.creatorId
                    )
                )
                (currentVersion(snippedId)).also(CurrentSnippetProjection::apply)
            }
        }
    }

    override fun update(snippetId: SnippetId, cmd: UpdateCmd): Snippet {
        return lock.withLock {
            if (commandAlreadyApplied(cmd.id, snippetId)) {
                versionOf(snippetId, cmd.id)
            } else {
                val currentVersion = versionOf(snippetId, cmd.id)
                store(
                    SnippetUpdatedRecord(
                        entityId = snippetId,
                        cmdId = cmd.id,
                        name = cmd.name ?: currentVersion.name,
                        inputs = cmd.inputs ?: currentVersion.inputs,
                        value = cmd.value ?: currentVersion.value
                    )
                )
                (currentVersion(snippetId)).also(CurrentSnippetProjection::apply)
            }
        }
    }

    override fun find(snippetId: SnippetId): Snippet? = CurrentSnippetProjection.find(snippetId)

    override fun list(query: SnippetQuery): List<Snippet> = CurrentSnippetProjection.list(query)

    override fun count(query: SnippetQuery): ULong = CurrentSnippetProjection.count(query)

    override fun clear() {
        super.clear()
        CurrentSnippetProjection.clear()
    }

    override fun close() {}
}