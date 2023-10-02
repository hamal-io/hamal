package io.hamal.repository.memory.record

import io.hamal.repository.api.*
import io.hamal.repository.record.code.CodeRecord
import io.hamal.repository.record.code.CreateCodeFromRecords
import java.util.concurrent.locks.ReentrantLock


class MemoryCodeRepository : MemoryRecordRepository<CodeId, CodeRecord, Code>(
    createDomainObject = CreateCodeFromRecords,
    recordClass = CodeRecord::class
), CodeRepository {
    private val lock = ReentrantLock()

    override fun create(cmd: CodeCmdRepository.CreateCmd): Code {
        TODO("Not yet implemented")
    }

    override fun update(codeId: CodeId, cmd: CodeCmdRepository.UpdateCmd): Code {
        TODO("Not yet implemented")
    }

    override fun close() {
        TODO("Not yet implemented")
    }

    override fun find(codeId: CodeId): Code? {
        TODO("Not yet implemented")
    }

    override fun list(query: CodeQueryRepository.CodeQuery): List<Code> {
        TODO("Not yet implemented")
    }

    override fun count(query: CodeQueryRepository.CodeQuery): ULong {
        TODO("Not yet implemented")
    }
}