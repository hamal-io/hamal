package io.hamal.repository.sqlite.record.feedback

import io.hamal.lib.common.domain.Count
import io.hamal.lib.common.domain.Count.Companion.Count
import io.hamal.lib.domain.vo.FeedbackId
import io.hamal.lib.sqlite.Connection
import io.hamal.repository.api.Feedback
import io.hamal.repository.api.FeedbackQueryRepository.FeedbackQuery
import io.hamal.repository.record.feedback.FeedbackRecord
import io.hamal.repository.sqlite.hon
import io.hamal.repository.sqlite.record.ProjectionSqlite
import io.hamal.repository.sqlite.record.RecordTransactionSqlite

internal object ProjectionCurrent : ProjectionSqlite.CurrentImpl<FeedbackId, FeedbackRecord, Feedback>() {

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
            set("data", hon.writeAndCompress(obj))
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
                hon.decompressAndRead(Feedback::class, rs.getBytes("data"))
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
                hon.decompressAndRead(Feedback::class, rs.getBytes("data"))
            }
        }
    }

    fun count(connection: Connection, query: FeedbackQuery): Count {
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

    private fun FeedbackQuery.ids(): String {
        return if (feedbackIds.isEmpty()) {
            ""
        } else {
            "AND id IN (${feedbackIds.joinToString(",") { "${it.longValue}" }})"
        }
    }
}