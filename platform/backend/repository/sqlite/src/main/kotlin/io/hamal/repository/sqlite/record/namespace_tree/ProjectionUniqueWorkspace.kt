package io.hamal.repository.sqlite.record.namespace_tree

import io.hamal.lib.domain.vo.NamespaceTreeId
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.Transaction
import io.hamal.repository.api.NamespaceTree
import io.hamal.repository.record.namespace_tree.NamespaceTreeRecord
import io.hamal.repository.sqlite.record.ProjectionSqlite
import io.hamal.repository.sqlite.record.RecordTransactionSqlite
import org.sqlite.SQLiteException

internal object ProjectionUniqueWorkspace : ProjectionSqlite<NamespaceTreeId, NamespaceTreeRecord, NamespaceTree> {

    override fun upsert(
        tx: RecordTransactionSqlite<NamespaceTreeId, NamespaceTreeRecord, NamespaceTree>,
        obj: NamespaceTree
    ) {
        try {
            tx.execute(
                """
                INSERT INTO unique_workspace (id,  workspace_id)  VALUES(:id, :workspaceId)
                    ON CONFLICT(id) DO UPDATE 
                        SET workspace_id=:workspaceId;
            """.trimIndent()
            ) {
                set("id", obj.id)
                set("workspaceId", obj.workspaceId)
            }
        } catch (e: SQLiteException) {
            if (e.message!!.contains("(UNIQUE constraint failed: unique_workspace.workspace_id)")) {
                throw IllegalArgumentException("NamespaceTree already exists in workspace")
            }
            throw e
        }
    }

    override fun setupSchema(connection: Connection) {
        connection.execute(
            """
            CREATE TABLE IF NOT EXISTS unique_workspace (
                 id             INTEGER NOT NULL,
                 workspace_id   INTEGER NOT NULL,
                 PRIMARY KEY    (id),
                 UNIQUE (workspace_id)
            );
        """.trimIndent()
        )
    }

    override fun clear(tx: Transaction) {
        tx.execute("""DELETE FROM unique_workspace""")
    }

}
