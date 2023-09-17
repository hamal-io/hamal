package io.hamal.repository.sqlite.record.trigger

import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.vo.TriggerId
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.Transaction
import io.hamal.lib.sqlite.unsafeInCriteria
import io.hamal.repository.api.EventTrigger
import io.hamal.repository.api.FixedRateTrigger
import io.hamal.repository.api.Trigger
import io.hamal.repository.api.TriggerQueryRepository
import io.hamal.repository.record.trigger.TriggerRecord
import io.hamal.repository.sqlite.record.Projection
import io.hamal.repository.sqlite.record.RecordTransaction
import io.hamal.repository.sqlite.record.protobuf
import kotlinx.serialization.ExperimentalSerializationApi


@OptIn(ExperimentalSerializationApi::class)
internal object ProjectionCurrent : Projection<TriggerId, TriggerRecord, Trigger> {
    fun find(connection: Connection, triggerId: TriggerId): Trigger? {
        return connection.executeQueryOne(
            """
            SELECT 
                data
             FROM
                current
            WHERE
                id  = :id
        """.trimIndent()
        ) {
            query {
                set("id", triggerId)
            }
            map { rs ->
                protobuf.decodeFromByteArray(Trigger.serializer(), rs.getBytes("data"))
            }
        }
    }

    fun list(
        connection: Connection,
        query: TriggerQueryRepository.TriggerQuery
    ): List<Trigger> {
        return connection.executeQuery<Trigger>(
            """
            SELECT 
                data
             FROM
                current
            WHERE
                id < :afterId AND
                ${unsafeInCriteria("type", query.types.map { it.value })}
            ORDER BY id DESC
            LIMIT :limit
        """.trimIndent()
        ) {
            query {
                set("afterId", query.afterId)
                set("limit", query.limit)
            }
            map { rs ->
                protobuf.decodeFromByteArray(Trigger.serializer(), rs.getBytes("data"))
            }
        }
    }

    override fun upsert(tx: RecordTransaction<TriggerId, TriggerRecord, Trigger>, obj: Trigger) {
        tx.execute(
            """
                INSERT OR REPLACE INTO current
                    (id,type, data) 
                VALUES
                    (:id,:type, :data)
            """.trimIndent()
        ) {
            set("id", obj.id)
            set(
                "type", when (obj) {
                    is FixedRateTrigger -> TriggerType.FixedRate
                    is EventTrigger -> TriggerType.Event
                }.value
            )
            set("data", protobuf.encodeToByteArray(Trigger.serializer(), obj))
        }
    }

    override fun setupSchema(connection: Connection) {
        connection.execute(
            """
            CREATE TABLE IF NOT EXISTS current (
                 id             INTEGER NOT NULL,
                 type           INTEGER NOT NULL,
                 data           BLOB NOT NULL,
                 PRIMARY KEY    (id)
            );
        """.trimIndent()
        )
    }

    override fun clear(tx: Transaction) {
        tx.execute("""DELETE FROM current""")
    }

    override fun invalidate() {
    }

}
