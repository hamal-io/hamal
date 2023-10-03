package io.hamal.repository.memory.record

import io.hamal.repository.api.*
import io.hamal.repository.api.CodeQueryRepository.CodeQuery
import io.hamal.repository.record.code.CodeCreationRecord
import io.hamal.repository.record.code.CodeRecord
import io.hamal.repository.record.code.CodeUpdatedRecord
import io.hamal.repository.record.code.CreateCodeFromRecords
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock


internal object CurrentCodeProjection {
    private val projection = mutableMapOf<CodeId, Code>()
    fun apply(code: Code) {
        if (projection.values.any { it.code == code.code }) {
            throw RuntimeException("CodeValue already exists at: ${code.id}")
        } else {
            projection[code.id] = code
        }
    }

    fun find(codeId: CodeId): Code? = projection[codeId]

    fun list(query: CodeQuery): List<Code> {
        return projection.filter { query.codeIds.isEmpty() || it.key in query.codeIds }
            .map { it.value }
            .reversed()
            .asSequence()
            .filter {
                if (query.groupIds.isEmpty()) true else query.groupIds.contains(it.groupId)
            }.dropWhile { it.id >= query.afterId }
            .take(query.limit.value)
            .toList()
    }

    fun count(query: CodeQuery): ULong {
        return projection.filter { query.codeIds.isEmpty() || it.key in query.codeIds }
            .map { it.value }
            .reversed()
            .asSequence()
            .filter {
                if (query.groupIds.isEmpty()) true else query.groupIds.contains(it.groupId)
            }.dropWhile { it.id >= query.afterId }
            .count()
            .toULong()
    }

    fun clear() {
        projection.clear()
    }
}


class MemoryCodeRepository : MemoryRecordRepository<CodeId, CodeRecord, Code>(
    createDomainObject = CreateCodeFromRecords,
    recordClass = CodeRecord::class
), CodeRepository {
    private val lock = ReentrantLock()

    override fun create(cmd: CodeCmdRepository.CreateCmd): Code {
        return lock.withLock {
            val codeId = cmd.codeId
            val cmdId = cmd.id
            if (commandAlreadyApplied(cmdId, codeId)) {
                versionOf(codeId, cmd.id)
            } else {
                store(
                    CodeCreationRecord(
                        cmdId = cmd.id,
                        entityId = codeId,
                        groupId = cmd.groupId,
                        code = cmd.code
                    )
                )
                (currentVersion(codeId)).also(CurrentCodeProjection::apply)
            }


        }
    }

    override fun update(codeId: CodeId, cmd: CodeCmdRepository.UpdateCmd): Code {
        return lock.withLock {
            if (commandAlreadyApplied(cmd.id, codeId)) {
                versionOf(codeId, cmd.id)
            } else {
                val currentVersion = versionOf(codeId, cmd.id)
                store(
                    CodeUpdatedRecord(
                        entityId = codeId,
                        cmdId = cmd.id,
                        code = cmd.code ?: currentVersion.code
                    )
                )
                (currentVersion(codeId)).also(CurrentCodeProjection::apply)
            }
        }
    }

    override fun close() {

    }

    override fun find(codeId: CodeId): Code? = CurrentCodeProjection.find(codeId)

    override fun list(query: CodeQuery): List<Code> = CurrentCodeProjection.list(query)

    override fun count(query: CodeQuery): ULong = CurrentCodeProjection.count(query)

    override fun clear() {
        super.clear()
        CurrentCodeProjection.clear()
    }
}