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
import io.hamal.repository.sqlite.record.SqliteProjection
import io.hamal.repository.sqlite.record.SqliteRecordTransaction
import io.hamal.repository.sqlite.record.protobuf
import kotlinx.serialization.ExperimentalSerializationApi


@OptIn(ExperimentalSerializationApi::class)
internal object ProjectionCurrent : SqliteProjection<TriggerId, TriggerRecord, Trigger> {
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
                ${query.ids()}
                ${query.groupIds()}
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

    fun count(
        connection: Connection,
        query: TriggerQueryRepository.TriggerQuery
    ): ULong {
        return connection.executeQueryOne(
            """
            SELECT 
                COUNT(*) as count 
            FROM 
                current
            WHERE
                id < :afterId
                ${query.ids()}
                ${query.groupIds()}
        """.trimIndent()
        ) {
            query {
                set("afterId", query.afterId)
            }
            map {
                it.getLong("count").toULong()
            }
        } ?: 0UL
    }

    override fun upsert(tx: SqliteRecordTransaction<TriggerId, TriggerRecord, Trigger>, obj: Trigger) {
        tx.execute(
            """
                INSERT OR REPLACE INTO current
                    (id, group_id, type, data) 
                VALUES
                    (:id, :groupId, :type, :data)
            """.trimIndent()
        ) {
            set("id", obj.id)
            set("groupId", obj.groupId)
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
                 group_id       INTEGER NOT NULL,
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

    private fun TriggerQueryRepository.TriggerQuery.groupIds(): String {
        return if (groupIds.isEmpty()) {
            ""
        } else {
            "AND group_id IN (${groupIds.joinToString(",") { "${it.value.value}" }})"
        }
    }

    private fun TriggerQueryRepository.TriggerQuery.ids(): String {
        return if (triggerIds.isEmpty()) {
            ""
        } else {
            "AND id IN (${triggerIds.joinToString(",") { "${it.value.value}" }})"
        }
    }
}
