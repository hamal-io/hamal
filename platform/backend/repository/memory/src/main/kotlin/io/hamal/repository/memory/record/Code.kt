package io.hamal.repository.memory.record

import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain.vo.CodeId
import io.hamal.lib.domain.vo.CodeVersion
import io.hamal.repository.api.Code
import io.hamal.repository.api.CodeCmdRepository.CreateCmd
import io.hamal.repository.api.CodeCmdRepository.UpdateCmd
import io.hamal.repository.api.CodeQueryRepository.CodeQuery
import io.hamal.repository.api.CodeRepository
import io.hamal.repository.record.RecordSequence
import io.hamal.repository.record.code.CodeRecord
import io.hamal.repository.record.code.CreateCodeFromRecords
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock


private object CodeCurrentProjection {
    private val projection = mutableMapOf<CodeId, Code>()
    fun apply(code: Code) {
        projection[code.id] = code
    }

    fun find(codeId: CodeId): Code? = projection[codeId]

    fun list(query: CodeQuery): List<Code> {
        return projection.filter { query.codeIds.isEmpty() || it.key in query.codeIds }
            .map { it.value }
            .reversed()
            .asSequence()
            .filter {
                if (query.workspaceIds.isEmpty()) true else query.workspaceIds.contains(it.workspaceId)
            }.dropWhile { it.id >= query.afterId }
            .take(query.limit.value)
            .toList()
    }

    fun count(query: CodeQuery): Count {
        return Count(
            projection.filter { query.codeIds.isEmpty() || it.key in query.codeIds }
                .map { it.value }
                .reversed()
                .asSequence()
                .filter {
                    if (query.workspaceIds.isEmpty()) true else query.workspaceIds.contains(it.workspaceId)
                }.dropWhile { it.id >= query.afterId }
                .count()
                .toLong()
        )
    }

    fun clear() {
        projection.clear()
    }
}


class CodeMemoryRepository : RecordMemoryRepository<CodeId, CodeRecord, Code>(
    createDomainObject = CreateCodeFromRecords,
    recordClass = CodeRecord::class
), CodeRepository {
    private val lock = ReentrantLock()

    override fun create(cmd: CreateCmd): Code {
        return lock.withLock {
            val codeId = cmd.codeId
            val cmdId = cmd.id
            if (commandAlreadyApplied(cmdId, codeId)) {
                versionOf(codeId, cmd.id)
            } else {
                store(
                    CodeRecord.Created(
                        cmdId = cmd.id,
                        entityId = codeId,
                        workspaceId = cmd.workspaceId,
                        value = cmd.value,
                        type = cmd.type
                    )
                )
                (currentVersion(codeId))
                    .also(CodeCurrentProjection::apply)
            }
        }
    }

    override fun update(codeId: CodeId, cmd: UpdateCmd): Code {
        return lock.withLock {
            if (commandAlreadyApplied(cmd.id, codeId)) {
                versionOf(codeId, cmd.id)
            } else {
                val currentVersion = versionOf(codeId, cmd.id)
                val codeValue = cmd.value
                return if (codeValue == null || codeValue == currentVersion.value) {
                    currentVersion
                } else {
                    store(
                        CodeRecord.Updated(
                            entityId = codeId,
                            cmdId = cmd.id,
                            value = codeValue
                        )
                    )
                    (currentVersion(codeId)).also(CodeCurrentProjection::apply)
                }
            }
        }
    }

    override fun close() {
    }

    override fun find(codeId: CodeId): Code? = lock.withLock { CodeCurrentProjection.find(codeId) }

    override fun find(codeId: CodeId, codeVersion: CodeVersion): Code? = lock.withLock {
        versionOf(codeId, RecordSequence(codeVersion.value))
    }

    override fun list(query: CodeQuery): List<Code> = lock.withLock { CodeCurrentProjection.list(query) }

    override fun count(query: CodeQuery): Count = lock.withLock { CodeCurrentProjection.count(query) }

    override fun clear() {
        lock.withLock {
            super.clear()
            CodeCurrentProjection.clear()
        }
    }
}