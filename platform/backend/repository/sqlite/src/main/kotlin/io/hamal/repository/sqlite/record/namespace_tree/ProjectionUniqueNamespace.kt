package io.hamal.repository.sqlite.record.namespace_tree

import io.hamal.lib.domain.vo.NamespaceTreeId
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.Transaction
import io.hamal.repository.api.NamespaceTree
import io.hamal.repository.record.namespace_tree.NamespaceTreeRecord
import io.hamal.repository.sqlite.record.ProjectionSqlite
import io.hamal.repository.sqlite.record.RecordTransactionSqlite
import org.sqlite.SQLiteException

internal object ProjectionUniqueNamespace : ProjectionSqlite<NamespaceTreeId, NamespaceTreeRecord, NamespaceTree> {

    override fun upsert(
        tx: RecordTransactionSqlite<NamespaceTreeId, NamespaceTreeRecord, NamespaceTree>,
        obj: NamespaceTree
    ) {
        try {
            tx.execute(
                """
                DELETE FROM unique_namespace where id = :id
            """.trimIndent()
            ) {
                set("id", obj.id)
            }

            obj.root.preorder().forEach { namespaceId ->
                tx.execute("""
                INSERT INTO unique_namespace (id,  namespace_id)  VALUES(:id, :namespaceId)
            """.trimIndent()
                ) {
                    set("id", obj.id)
                    set("namespaceId", namespaceId)
                }
            }
        } catch (e: SQLiteException) {
            if (e.message!!.contains("(UNIQUE constraint failed: unique_namespace.id, unique_namespace.namespace_id)")) {
                throw IllegalArgumentException("Namespace already exists in NamespaceTree")
            }
            throw e
        }
    }

    override fun setupSchema(connection: Connection) {
        connection.execute(
            """
            CREATE TABLE IF NOT EXISTS unique_namespace (
                 id             INTEGER NOT NULL,
                 namespace_id   INTEGER NOT NULL,
                 PRIMARY KEY    (id, namespace_id)
            );
        """.trimIndent()
        )
    }

    override fun clear(tx: Transaction) {
        tx.execute("""DELETE FROM unique_namespace""")
    }

}
