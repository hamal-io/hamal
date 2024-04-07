package com.nyanbot.repository.impl.account

import com.nyanbot.json
import com.nyanbot.repository.Account
import com.nyanbot.repository.AccountQueryRepository.AccountQuery
import com.nyanbot.repository.impl.ProjectionSqlite
import com.nyanbot.repository.impl.RecordTransactionSqlite
import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.Transaction


internal object ProjectionCurrent : ProjectionSqlite<AccountId, AccountRecord, Account> {

    fun find(connection: Connection, accountId: AccountId): Account? {
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
                set("id", accountId)
            }
            map { rs ->
                json.decompressAndDeserialize(Account::class, rs.getBytes("data"))
            }
        }
    }

    fun list(connection: Connection, query: AccountQuery): List<Account> {
        return connection.executeQuery<Account>(
            """
            SELECT 
                data
             FROM
                current
            WHERE
                id < :afterId
                ${query.ids()}
            ORDER BY id DESC
            LIMIT :limit
        """.trimIndent()
        ) {
            query {
                set("afterId", query.afterId)
                set("limit", query.limit)
            }
            map { rs ->
                json.decompressAndDeserialize(Account::class, rs.getBytes("data"))
            }
        }
    }

    fun count(connection: Connection, query: AccountQuery): Count {
        return Count(
            connection.executeQueryOne(
                """
            SELECT 
                COUNT(*) as count 
            FROM 
                current
            WHERE
                id < :afterId
                ${query.ids()}
        """.trimIndent()
            ) {
                query {
                    set("afterId", query.afterId)
                }
                map {
                    it.getLong("count")
                }
            } ?: 0L
        )
    }

    override fun upsert(tx: RecordTransactionSqlite<AccountId, AccountRecord, Account>, obj: Account) {
        tx.execute(
            """
                INSERT OR REPLACE INTO current
                    (id, data) 
                VALUES
                    (:id, :data)
            """.trimIndent()
        ) {
            set("id", obj.id)
            set("data", json.serializeAndCompress(obj))
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

    override fun clear(tx: Transaction) {
        tx.execute("""DELETE FROM current""")
    }

    private fun AccountQuery.ids(): String {
        return if (accountIds.isEmpty()) {
            ""
        } else {
            "AND id IN (${accountIds.joinToString(",") { "${it.value.value}" }})"
        }
    }
}
