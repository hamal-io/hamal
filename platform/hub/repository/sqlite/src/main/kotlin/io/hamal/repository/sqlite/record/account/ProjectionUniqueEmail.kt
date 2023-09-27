package io.hamal.repository.sqlite.record.account

import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.Transaction
import io.hamal.repository.api.Account
import io.hamal.repository.record.account.AccountRecord
import io.hamal.repository.sqlite.record.SqliteProjection
import io.hamal.repository.sqlite.record.SqliteRecordTransaction
import org.sqlite.SQLiteException

internal object ProjectionUniqueEmail : SqliteProjection<AccountId, AccountRecord, Account> {

    override fun upsert(tx: SqliteRecordTransaction<AccountId, AccountRecord, Account>, obj: Account) {
        val email = obj.email
        if (email != null) {
            try {
                tx.execute(
                    """
                INSERT INTO unique_email (id, email)  VALUES(:id, :email)
                    ON CONFLICT(id) DO UPDATE 
                        SET email=:email;
            """.trimIndent()
                ) {
                    set("id", obj.id)
                    set("email", email.value)
                }
            } catch (e: SQLiteException) {
                if (e.message!!.contains("(UNIQUE constraint failed: unique_email.email)")) {
                    throw IllegalArgumentException("${obj.email} already exists")
                }
                throw e
            }
        }
    }

    override fun setupSchema(connection: Connection) {
        connection.execute(
            """
            CREATE TABLE IF NOT EXISTS unique_email (
                 id             INTEGER NOT NULL,
                 email           VARCHAR(255) UNIQUE NOT NULL,
                 PRIMARY KEY    (id)
            );
        """.trimIndent()
        )
    }

    override fun clear(tx: Transaction) {
        tx.execute("""DELETE FROM unique_email""")
    }

    override fun invalidate() {}
}
