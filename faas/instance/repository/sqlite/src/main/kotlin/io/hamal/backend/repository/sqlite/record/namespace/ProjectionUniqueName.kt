package io.hamal.backend.repository.sqlite.record.namespace

import io.hamal.backend.repository.api.Namespace
import io.hamal.backend.repository.record.namespace.NamespaceRecord
import io.hamal.backend.repository.sqlite.record.Projection
import io.hamal.backend.repository.sqlite.record.RecordTransaction
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.NamespaceName
import io.hamal.lib.sqlite.Connection

internal object ProjectionUniqueName : Projection<NamespaceId, NamespaceRecord, Namespace> {

    fun find(connection: Connection, namespaceName: NamespaceName): NamespaceId? {
        return connection.executeQueryOne(
            """
            SELECT 
                id
             FROM
                unique_names
            WHERE
                name  = :name
        """.trimIndent()
        ) {
            query {
                set("name", namespaceName)
            }
            map { rs -> rs.getDomainId("id", ::NamespaceId) }
        }
    }

    override fun upsert(tx: RecordTransaction<NamespaceId, NamespaceRecord, Namespace>, obj: Namespace) {
        tx.execute(
            """
                INSERT INTO unique_names
                    (id, name) 
                VALUES
                    (:id, :name)
            """.trimIndent()
        ) {
            set("id", obj.id)
            set("name", obj.name)
        }
    }

    override fun setupSchema(connection: Connection) {
        connection.execute(
            """
            CREATE TABLE IF NOT EXISTS unique_names (
                 id             INTEGER NOT NULL,
                 name           VARCHAR(255) UNIQUE NOT NULL,
                 PRIMARY KEY    (id)
            );
        """.trimIndent()
        )
    }

    override fun clear(connection: Connection) {
        connection.execute("""DELETE FROM unique_names""")
    }

    override fun invalidate() {}
}
