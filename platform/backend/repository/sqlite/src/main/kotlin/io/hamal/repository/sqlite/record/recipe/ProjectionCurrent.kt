package io.hamal.repository.sqlite.record.recipe

import io.hamal.lib.common.domain.Count
import io.hamal.lib.common.domain.Count.Companion.Count
import io.hamal.lib.domain.vo.RecipeId
import io.hamal.lib.sqlite.Connection
import io.hamal.repository.api.Recipe
import io.hamal.repository.api.RecipeQueryRepository.RecipeQuery
import io.hamal.repository.record.recipe.RecipeRecord
import io.hamal.repository.sqlite.hon
import io.hamal.repository.sqlite.record.ProjectionSqlite
import io.hamal.repository.sqlite.record.RecordTransactionSqlite

object ProjectionCurrent : ProjectionSqlite.CurrentImpl<RecipeId, RecipeRecord, Recipe>() {


    override fun upsert(tx: RecordTransactionSqlite<RecipeId, RecipeRecord, Recipe>, obj: Recipe) {
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

    fun find(connection: Connection, recipeId: RecipeId): Recipe? {
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
                set("id", recipeId)
            }
            map { rs ->
                hon.decompressAndRead(Recipe::class, rs.getBytes("data"))
            }
        }
    }

    fun list(connection: Connection, query: RecipeQuery): List<Recipe> {
        return connection.executeQuery<Recipe>(
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
                hon.decompressAndRead(Recipe::class, rs.getBytes("data"))
            }
        }
    }

    fun count(connection: Connection, query: RecipeQuery): Count {
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

    private fun RecipeQuery.ids(): String {
        return if (recipeIds.isEmpty()) {
            ""
        } else {
            "AND id IN (${recipeIds.joinToString(",") { "${it.longValue}" }})"
        }
    }
}