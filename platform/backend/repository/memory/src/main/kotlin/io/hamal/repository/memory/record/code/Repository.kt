package io.hamal.repository.memory.record.code

import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain.vo.CodeId
import io.hamal.lib.domain.vo.CodeVersion
import io.hamal.repository.api.Code
import io.hamal.repository.api.CodeCmdRepository.CreateCmd
import io.hamal.repository.api.CodeCmdRepository.UpdateCmd
import io.hamal.repository.api.CodeQueryRepository.CodeQuery
import io.hamal.repository.api.CodeRepository
import io.hamal.repository.memory.record.RecordMemoryRepository
import io.hamal.repository.record.RecordSequence
import io.hamal.repository.record.code.CodeRecord
import io.hamal.repository.record.code.CreateCodeFromRecords
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock


class CodeMemoryRepository : RecordMemoryRepository<CodeId, CodeRecord, Code>(
    createDomainObject = CreateCodeFromRecords,
    recordClass = CodeRecord::class,
    projections = listOf(ProjectionCurrent())
), CodeRepository {

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
                (currentVersion(codeId)).also(currentProjection::upsert)
            }
        }
    }

    override fun update(codeId: CodeId, cmd: UpdateCmd): Code {
        return lock.withLock {
            if (commandAlreadyApplied(cmd.id, codeId)) {
                versionOf(codeId, cmd.id)
            } else {
                val currentVersion = versionOf(codeId, cmd.id)
                val ValueCode = cmd.value
                return if (ValueCode == null || ValueCode == currentVersion.value) {
                    currentVersion
                } else {
                    store(
                        CodeRecord.Updated(
                            entityId = codeId,
                            cmdId = cmd.id,
                            value = ValueCode
                        )
                    )
                    (currentVersion(codeId)).also(currentProjection::upsert)
                }
            }
        }
    }

    override fun close() { }

    override fun find(codeId: CodeId): Code? = lock.withLock { currentProjection.find(codeId) }

    override fun find(codeId: CodeId, codeVersion: CodeVersion): Code? = lock.withLock {
        versionOf(codeId, RecordSequence(codeVersion.value))
    }

    override fun list(query: CodeQuery): List<Code> = lock.withLock { currentProjection.list(query) }

    override fun count(query: CodeQuery): Count = lock.withLock { currentProjection.count(query) }

    private val lock = ReentrantLock()
    private val currentProjection = getProjection<ProjectionCurrent>()
}