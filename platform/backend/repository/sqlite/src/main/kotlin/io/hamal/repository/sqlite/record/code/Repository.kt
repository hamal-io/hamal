package io.hamal.repository.sqlite.record.code

import io.hamal.lib.domain.vo.CodeId
import io.hamal.lib.domain.vo.CodeVersion
import io.hamal.lib.sqlite.SqliteBaseRepository
import io.hamal.repository.api.Code
import io.hamal.repository.api.CodeCmdRepository.CreateCmd
import io.hamal.repository.api.CodeCmdRepository.UpdateCmd
import io.hamal.repository.api.CodeQueryRepository.CodeQuery
import io.hamal.repository.api.CodeRepository
import io.hamal.repository.record.CreateDomainObject
import io.hamal.repository.record.RecordSequence
import io.hamal.repository.record.code.CodeCreatedRecord
import io.hamal.repository.record.code.CodeEntity
import io.hamal.repository.record.code.CodeRecord
import io.hamal.repository.record.code.CodeUpdatedRecord
import io.hamal.repository.sqlite.record.SqliteRecordRepository
import java.nio.file.Path


internal object CreateCode : CreateDomainObject<CodeId, CodeRecord, Code> {
    override fun invoke(recs: List<CodeRecord>): Code {
        check(recs.isNotEmpty()) { "At least one record is required" }
        val firstRecord = recs.first()
        check(firstRecord is CodeCreatedRecord)

        var result = CodeEntity(
            cmdId = firstRecord.cmdId,
            id = firstRecord.entityId,
            groupId = firstRecord.groupId,
            sequence = firstRecord.sequence()
        )

        recs.forEach { record ->
            result = result.apply(record)
        }

        return result.toDomainObject()
    }
}

class SqliteCodeRepository(
    config: Config
) : SqliteRecordRepository<CodeId, CodeRecord, Code>(
    config = config,
    createDomainObject = CreateCode,
    recordClass = CodeRecord::class,
    projections = listOf(ProjectionCurrent)
), CodeRepository {

    data class Config(
        override val path: Path
    ) : SqliteBaseRepository.Config {
        override val filename = "code.db"
    }

    override fun create(cmd: CreateCmd): Code {
        val codeId = cmd.codeId
        val cmdId = cmd.id
        return tx {
            if (commandAlreadyApplied(cmdId, codeId)) {
                versionOf(codeId, cmdId)
            } else {
                store(
                    CodeCreatedRecord(
                        cmdId = cmdId,
                        entityId = codeId,
                        groupId = cmd.groupId,
                        value = cmd.value,
                        type = cmd.type
                    )
                )

                currentVersion(codeId)
                    .also { ProjectionCurrent.upsert(this, it) }
            }
        }
    }

    override fun update(codeId: CodeId, cmd: UpdateCmd): Code {
        val cmdId = cmd.id
        return tx {
            if (commandAlreadyApplied(cmdId, codeId)) {
                versionOf(codeId, cmdId)
            } else {
                val currentVersion = versionOf(codeId, cmdId)
                if (cmd.value != null && cmd.value != currentVersion.value) {
                    store(
                        CodeUpdatedRecord(
                            cmdId = cmdId,
                            entityId = codeId,
                            value = cmd.value!!
                        )
                    )
                }
                currentVersion(codeId)
                    .also { ProjectionCurrent.upsert(this, it) }

            }
        }
    }

    override fun find(codeId: CodeId): Code? {
        return ProjectionCurrent.find(connection, codeId)
    }

    override fun find(codeId: CodeId, codeVersion: CodeVersion): Code? {
        return tx { versionOf(codeId, RecordSequence(codeVersion.value)) }
    }

    override fun list(query: CodeQuery): List<Code> {
        return ProjectionCurrent.list(connection, query)
    }

    override fun count(query: CodeQuery): ULong {
        return ProjectionCurrent.count(connection, query)
    }
}

