package io.hamal.repository.sqlite.record.code

import io.hamal.lib.domain.vo.CodeId
import io.hamal.lib.domain.vo.CodeVersion
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.Transaction
import io.hamal.repository.api.Code
import io.hamal.repository.api.CodeQueryRepository
import io.hamal.repository.record.code.CodeRecord
import io.hamal.repository.sqlite.record.SqliteProjection
import io.hamal.repository.sqlite.record.SqliteRecordTransaction
import kotlinx.serialization.ExperimentalSerializationApi

@OptIn(ExperimentalSerializationApi::class)
internal object ProjectionCurrent : SqliteProjection<CodeId, CodeRecord, Code> {

    fun find(connection: Connection, codeID: CodeId): Code? {
        TODO("Not yet implemented")
    }

    fun find(connection: Connection, codeId: CodeId, codeVersion: CodeVersion): Code? {
        TODO("Not yet implemented")
    }

    fun list(connection: Connection, query: CodeQueryRepository.CodeQuery): List<Code> {
        TODO("Not yet implemented")
    }

    fun count(connection: Connection, query: CodeQueryRepository.CodeQuery): ULong {
        TODO("Not yet implemented")
    }

    override fun upsert(tx: SqliteRecordTransaction<CodeId, CodeRecord, Code>, obj: Code) {
        TODO("Not yet implemented")
    }

    override fun setupSchema(connection: Connection) {
        TODO("Not yet implemented")
    }

    override fun clear(tx: Transaction) {
        TODO("Not yet implemented")
    }
}