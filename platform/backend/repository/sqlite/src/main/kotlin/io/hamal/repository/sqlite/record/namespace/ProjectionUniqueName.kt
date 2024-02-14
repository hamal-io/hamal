package io.hamal.repository.sqlite.record.namespace

import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.NamespaceName
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.Transaction
import io.hamal.repository.api.Namespace
import io.hamal.repository.record.namespace.NamespaceRecord
import io.hamal.repository.sqlite.record.ProjectionSqlite
import io.hamal.repository.sqlite.record.RecordTransactionSqlite
import org.sqlite.SQLiteException

internal object ProjectionUniqueName : ProjectionSqlite<NamespaceId, NamespaceRecord, Namespace> {

    fun find(connection: Connection, namespaceName: NamespaceName): NamespaceId? {
        return connection.executeQueryOne(
            """
            SELECT 
                id
             FROM
                unique_name
            WHERE
                name  = :name
        """.trimIndent()
        ) {
            query {
                set("name", namespaceName)
            }
            map { rs -> rs.getId("id", ::NamespaceId) }
        }
    }

    override fun upsert(tx: RecordTransactionSqlite<NamespaceId, NamespaceRecord, Namespace>, obj: Namespace) {
        try {
            tx.execute(
                """
                INSERT INTO unique_name (id, workspace_id, name)  VALUES(:id, :workspaceId, :name)
                    ON CONFLICT(id) DO UPDATE 
                        SET name=:name;
            """.trimIndent()
            ) {
                set("id", obj.id)
                set("workspaceId", obj.workspaceId)
                set("name", obj.name)
            }
        } catch (e: SQLiteException) {
            if (e.message!!.contains("(UNIQUE constraint failed: unique_name.workspace_id, unique_name.name)")) {
                throw IllegalArgumentException("${obj.name} already exists")
            }
            throw e
        }
    }

    override fun setupSchema(connection: Connection) {
        connection.execute(
            """
            CREATE TABLE IF NOT EXISTS unique_name (
                 id             INTEGER NOT NULL,
                 workspace_id       INTEGER NOT NULL,
                 name           VARCHAR(255) NOT NULL,
                 PRIMARY KEY    (id),
                 UNIQUE (workspace_id, name)
            );
        """.trimIndent()
        )
    }

    override fun clear(tx: Transaction) {
        tx.execute("""DELETE FROM unique_name""")
    }

}
