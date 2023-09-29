package io.hamal.repository.memory

import io.hamal.lib.domain.vo.FuncId
import io.hamal.repository.api.*
import io.hamal.repository.memory.record.MemoryRecordRepository
import kotlinx.serialization.Serializable
import java.util.concurrent.locks.ReentrantLock
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.withLock

@Serializable
sealed class CodeRecord(

): Record<CodeId>()

class CreateCodeFromRecord{

}

class MemoryCodeRepository : MemoryRecordRepository<CodeId, CodeRecord, Code>(
    createDomainObject = CreateCodeFromRecords,
    recordClass = CodeRecord::class
), CodeRepository {
    private val map = mutableMapOf<CodeId, CodeValue>()
    private val lock = ReentrantLock()

    override fun create(cmd: CodeCmdRepository.CreateCmd): Code {
        TODO("Not yet implemented")
    }

    override fun update(): Code {
        TODO("Not yet implemented")
    }

    override fun clear() {
        TODO("Not yet implemented")
    }

    override fun close() {
        TODO("Not yet implemented")
    }

    override fun find(codeId: CodeId): Code? {
        TODO("Not yet implemented")
    }

    override fun list(): List<Code> {
        TODO("Not yet implemented")
    }

    override fun count(query: CodeQueryRepository.CodeQuery): ULong {
        TODO("Not yet implemented")
    }
}