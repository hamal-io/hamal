package io.hamal.repository.sqlite.record.workspace

import io.hamal.lib.domain.vo.WorkspaceId
import io.hamal.lib.domain.vo.WorkspaceName
import io.hamal.lib.sqlite.Connection
import io.hamal.repository.api.Workspace
import io.hamal.repository.record.workspace.WorkspaceRecord
import io.hamal.repository.sqlite.record.ProjectionSqlite
import io.hamal.repository.sqlite.record.RecordTransactionSqlite
import org.sqlite.SQLiteException

internal object ProjectionUniqueName : ProjectionSqlite.UniqueImpl<WorkspaceId, WorkspaceRecord, Workspace>("unique_name") {

    fun find(connection: Connection, workspaceName: WorkspaceName): WorkspaceId? {
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
                set("name", workspaceName)
            }
            map { rs -> rs.getId("id", ::WorkspaceId) }
        }
    }

    override fun upsert(tx: RecordTransactionSqlite<WorkspaceId, WorkspaceRecord, Workspace>, obj: Workspace) {
        try {
            tx.execute(
                """
                INSERT INTO unique_name (id, name)  VALUES(:id, :name)
                    ON CONFLICT(id) DO UPDATE 
                        SET name=:name;
            """.trimIndent()
            ) {
                set("id", obj.id)
                set("name", obj.name)
            }
        } catch (e: SQLiteException) {
            if (e.message!!.contains("(UNIQUE constraint failed: unique_name.name)")) {
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
                 name           VARCHAR(255) UNIQUE NOT NULL,
                 PRIMARY KEY    (id)
            );
        """.trimIndent()
        )
    }

}
