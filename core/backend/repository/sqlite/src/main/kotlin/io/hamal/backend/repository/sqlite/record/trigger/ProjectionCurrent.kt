package io.hamal.backend.repository.sqlite.record.trigger

import io.hamal.backend.repository.api.TriggerQueryRepository
import io.hamal.backend.repository.record.trigger.TriggerRecord
import io.hamal.backend.repository.sqlite.record.Projection
import io.hamal.backend.repository.sqlite.record.RecordTransaction
import io.hamal.backend.repository.sqlite.record.protobuf
import io.hamal.lib.common.DefaultLruCache
import io.hamal.lib.domain.Trigger
import io.hamal.lib.domain.vo.TriggerId
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.unsafeInCriteria
import kotlinx.serialization.ExperimentalSerializationApi


@OptIn(ExperimentalSerializationApi::class)
internal object ProjectionCurrent : Projection<TriggerId, TriggerRecord, Trigger> {
    internal val lruCache = DefaultLruCache<TriggerId, Trigger>(10_000)
    fun find(connection: Connection, triggerId: TriggerId): Trigger? {
        return lruCache.computeIfAbsent(triggerId) {
            connection.executeQueryOne(
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
                    set("id", it)
                }
                map { rs ->
                    protobuf.decodeFromByteArray(Trigger.serializer(), rs.getBytes("data"))
                }
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
                id > :afterId AND
                ${unsafeInCriteria("type", query.types.map { it.value })}
            ORDER BY id ASC
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

    override fun update(tx: RecordTransaction<TriggerId, TriggerRecord, Trigger>, obj: Trigger) {
        tx.execute(
            """
                INSERT OR REPLACE INTO current
                    (id,type, data) 
                VALUES
                    (:id,:type, :data)
            """.trimIndent()
        ) {
            set("id", obj.id)
            set("type", obj.type.value)
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

    override fun clear(connection: Connection) {
        connection.execute("""DELETE FROM current""")
    }

    override fun invalidate() {
        lruCache.clear()
    }
}
