package io.hamal.repository.sqlite.record.func

import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.Transaction
import io.hamal.repository.api.Func
import io.hamal.repository.record.func.FuncRecord
import io.hamal.repository.sqlite.record.SqliteProjection
import io.hamal.repository.sqlite.record.SqliteRecordTransaction
import org.sqlite.SQLiteException

internal object ProjectionUniqueName : SqliteProjection<FuncId, FuncRecord, Func> {

    override fun upsert(tx: SqliteRecordTransaction<FuncId, FuncRecord, Func>, obj: Func) {
        try {
            tx.execute(
                """
                INSERT INTO unique_name (id, name, namespace_id)  VALUES(:id, :name, :namespaceId)
                    ON CONFLICT(id) DO UPDATE 
                        SET name=:name, namespace_id=:namespaceId;
            """.trimIndent()
            ) {
                set("id", obj.id)
                set("name", obj.name)
                set("namespaceId", obj.namespaceId)
            }
        } catch (e: SQLiteException) {
            if (e.message!!.contains("(UNIQUE constraint failed: unique_name.name, unique_name.namespace_id)")) {
                throw IllegalArgumentException("${obj.name} already exists in namespace ${obj.namespaceId}")
            }
            throw e
        }
    }

    override fun setupSchema(connection: Connection) {
        connection.execute(
            """
            CREATE TABLE IF NOT EXISTS unique_name (
                 id             INTEGER NOT NULL,
                 name           VARCHAR(255) NOT NULL,
                 namespace_id   INTEGER NOT NULL,
                 PRIMARY KEY    (id),
                 UNIQUE (name, namespace_id)
            );
        """.trimIndent()
        )
    }

    override fun clear(tx: Transaction) {
        tx.execute("""DELETE FROM unique_name""")
    }

    override fun invalidate() {}
}
