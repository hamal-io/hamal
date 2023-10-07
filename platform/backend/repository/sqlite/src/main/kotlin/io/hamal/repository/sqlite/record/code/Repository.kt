package io.hamal.repository.sqlite.record.code

import io.hamal.lib.domain.vo.CodeId
import io.hamal.lib.domain.vo.CodeVersion
import io.hamal.lib.sqlite.SqliteBaseRepository
import io.hamal.repository.api.Code
import io.hamal.repository.api.CodeCmdRepository
import io.hamal.repository.api.CodeQueryRepository
import io.hamal.repository.api.CodeRepository
import io.hamal.repository.record.CreateDomainObject
import io.hamal.repository.record.code.CodeCreationRecord
import io.hamal.repository.record.code.CodeEntity
import io.hamal.repository.record.code.CodeRecord
import io.hamal.repository.sqlite.record.SqliteRecordRepository

import java.nio.file.Path

internal object CreateCode : CreateDomainObject<CodeId, CodeRecord, Code> {
    override fun invoke(recs: List<CodeRecord>): Code {
        check(recs.isNotEmpty()) { "At least one record is required" }
        val firstRecord = recs.first()
        check(firstRecord is CodeCreationRecord)

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
        override val path: Path,

        ) : SqliteBaseRepository.Config {
        override val filename = "code.db"
    }

    override fun create(cmd: CodeCmdRepository.CreateCmd): Code {
        TODO("Not yet implemented")
    }

    override fun update(codeId: CodeId, cmd: CodeCmdRepository.UpdateCmd): Code {
        TODO("Not yet implemented")
    }

    override fun find(codeId: CodeId): Code? {
        TODO("Not yet implemented")
    }

    override fun find(codeId: CodeId, codeVersion: CodeVersion): Code? {
        TODO("Not yet implemented")
    }

    override fun list(query: CodeQueryRepository.CodeQuery): List<Code> {
        TODO("Not yet implemented")
    }

    override fun count(query: CodeQueryRepository.CodeQuery): ULong {
        TODO("Not yet implemented")
    }
}
