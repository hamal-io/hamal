package io.hamal.repository.sqlite.record.feedback

import io.hamal.lib.domain.Json
import io.hamal.lib.domain.vo.FeedbackId
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.Transaction
import io.hamal.repository.api.Feedback
import io.hamal.repository.api.FeedbackQueryRepository.FeedbackQuery
import io.hamal.repository.record.feedback.FeedbackRecord
import io.hamal.repository.sqlite.record.ProjectionSqlite
import io.hamal.repository.sqlite.record.RecordTransactionSqlite

internal object ProjectionCurrent : ProjectionSqlite<FeedbackId, FeedbackRecord, Feedback> {
    override fun upsert(tx: RecordTransactionSqlite<FeedbackId, FeedbackRecord, Feedback>, obj: Feedback) {
        tx.execute(
            """
                INSERT OR REPLACE INTO current
                    (id, data) 
                VALUES
                    (:id, :data)
            """.trimIndent()
        ) {
            set("id", obj.id)
            set("data", Json.serializeAndCompress(obj))
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

    fun find(connection: Connection, feedbackId: FeedbackId): Feedback? {
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
                set("id", feedbackId)
            }
            map { rs ->
                Json.decompressAndDeserialize(Feedback::class, rs.getBytes("data"))
            }
        }
    }

    fun list(connection: Connection, query: FeedbackQuery): List<Feedback> {
        return connection.executeQuery<Feedback>(
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
                Json.decompressAndDeserialize(Feedback::class, rs.getBytes("data"))
            }
        }
    }

    fun count(connection: Connection, query: FeedbackQuery): ULong {
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

    private fun FeedbackQuery.ids(): String {
        return if (feedbackIds.isEmpty()) {
            ""
        } else {
            "AND id IN (${feedbackIds.joinToString(",") { "${it.value.value}" }})"
        }
    }
}