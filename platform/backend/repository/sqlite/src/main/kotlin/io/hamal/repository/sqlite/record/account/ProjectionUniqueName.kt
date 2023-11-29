package io.hamal.repository.sqlite.record.account

import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.AccountName
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.Transaction
import io.hamal.repository.api.Account
import io.hamal.repository.record.account.AccountRecord
import io.hamal.repository.sqlite.record.ProjectionSqlite
import io.hamal.repository.sqlite.record.RecordTransactionSqlite
import org.sqlite.SQLiteException

internal object ProjectionUniqueName : ProjectionSqlite<AccountId, AccountRecord, Account> {

    fun find(connection: Connection, accountName: AccountName): AccountId? {
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
                set("name", accountName)
            }
            map { rs -> rs.getDomainId("id", ::AccountId) }
        }
    }

    override fun upsert(tx: RecordTransactionSqlite<AccountId, AccountRecord, Account>, obj: Account) {
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

    override fun clear(tx: Transaction) {
        tx.execute("""DELETE FROM unique_name""")
    }
}
