package io.hamal.backend.repository.sqlite.record.namespace

import io.hamal.backend.repository.api.Namespace
import io.hamal.backend.repository.record.namespace.NamespaceRecord
import io.hamal.backend.repository.sqlite.record.Projection
import io.hamal.backend.repository.sqlite.record.RecordTransaction
import io.hamal.backend.repository.sqlite.record.protobuf
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.sqlite.Connection
import kotlinx.serialization.ExperimentalSerializationApi


@OptIn(ExperimentalSerializationApi::class)
internal object ProjectionCurrent : Projection<NamespaceId, NamespaceRecord, Namespace> {

    fun find(connection: Connection, namespaceId: NamespaceId): Namespace? {
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
                set("id", namespaceId)
            }
            map { rs ->
                protobuf.decodeFromByteArray(Namespace.serializer(), rs.getBytes("data"))
            }
        }
    }

    fun list(connection: Connection, afterId: NamespaceId, limit: Limit): List<Namespace> {
        return connection.executeQuery<Namespace>(
            """
            SELECT 
                data
             FROM
                current
            WHERE
                id < :afterId
            ORDER BY id DESC
            LIMIT :limit
        """.trimIndent()
        ) {
            query {
                set("afterId", afterId)
                set("limit", limit)
            }
            map { rs ->
                protobuf.decodeFromByteArray(Namespace.serializer(), rs.getBytes("data"))
            }
        }
    }

    override fun upsert(tx: RecordTransaction<NamespaceId, NamespaceRecord, Namespace>, obj: Namespace) {
        tx.execute(
            """
                INSERT OR REPLACE INTO current
                    (id, data) 
                VALUES
                    (:id, :data)
            """.trimIndent()
        ) {
            set("id", obj.id)
            set("data", protobuf.encodeToByteArray(Namespace.serializer(), obj))
        }
    }

    override fun setupSchema(connection: Connection) {
        connection.execute(
            """
            CREATE TABLE IF NOT EXISTS current (
                 id             INTEGER NOT NULL,
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
    }
}
