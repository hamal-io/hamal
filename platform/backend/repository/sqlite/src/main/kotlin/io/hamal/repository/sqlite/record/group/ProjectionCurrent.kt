package io.hamal.repository.sqlite.record.group

import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.Transaction
import io.hamal.repository.api.Group
import io.hamal.repository.record.group.GroupRecord
import io.hamal.repository.sqlite.record.SqliteProjection
import io.hamal.repository.sqlite.record.SqliteRecordTransaction
import kotlinx.serialization.ExperimentalSerializationApi

@OptIn(ExperimentalSerializationApi::class)
internal object ProjectionCurrent : SqliteProjection<GroupId, GroupRecord, Group> {
    override fun upsert(tx: SqliteRecordTransaction<GroupId, GroupRecord, Group>, obj: Group) {
        TODO("Not yet implemented")
    }

    override fun setupSchema(connection: Connection) {
        TODO("Not yet implemented")
    }

    override fun clear(tx: Transaction) {
        TODO("Not yet implemented")
    }
}
