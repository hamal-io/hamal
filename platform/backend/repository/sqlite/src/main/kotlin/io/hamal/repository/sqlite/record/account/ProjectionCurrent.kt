package io.hamal.repository.sqlite.record.account

import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.Transaction
import io.hamal.repository.api.Account
import io.hamal.repository.api.AccountQueryRepository.AccountQuery
import io.hamal.repository.record.account.AccountRecord
import io.hamal.repository.sqlite.record.SqliteProjection
import io.hamal.repository.sqlite.record.SqliteRecordTransaction
import io.hamal.repository.sqlite.record.protobuf
import kotlinx.serialization.ExperimentalSerializationApi


@OptIn(ExperimentalSerializationApi::class)
internal object ProjectionCurrent : SqliteProjection<AccountId, AccountRecord, Account> {

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
                protobuf.decodeFromByteArray(Account.serializer(), rs.getBytes("data"))
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
                protobuf.decodeFromByteArray(Account.serializer(), rs.getBytes("data"))
            }
        }
    }

    fun count(connection: Connection, query: AccountQuery): ULong {
        return connection.executeQueryOne(
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
                it.getLong("count").toULong()
            }
        } ?: 0UL
    }

    override fun upsert(tx: SqliteRecordTransaction<AccountId, AccountRecord, Account>, obj: Account) {
        tx.execute(
            """
                INSERT OR REPLACE INTO current
                    (id, data) 
                VALUES
                    (:id, :data)
            """.trimIndent()
        ) {
            set("id", obj.id)
            set("data", protobuf.encodeToByteArray(Account.serializer(), obj))
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