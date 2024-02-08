package io.hamal.repository.sqlite

import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.Requested
import io.hamal.lib.domain.vo.RequestId
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.SqliteBaseRepository
import io.hamal.repository.api.RequestQueryRepository.ReqQuery
import io.hamal.repository.api.RequestRepository
import io.hamal.repository.record.json
import java.nio.file.Path

class RequestSqliteRepository(
    config: Config
) : SqliteBaseRepository(config), RequestRepository {

    data class Config(
        override val path: Path
    ) : SqliteBaseRepository.Config {
        override val filename = "request.db"
    }


    override fun setupConnection(connection: Connection) {
        connection.execute("""PRAGMA journal_mode = wal;""")
        connection.execute("""PRAGMA locking_mode = exclusive;""")
        connection.execute("""PRAGMA temp_store = memory;""")
        connection.execute("""PRAGMA synchronous = off;""")
    }


    override fun setupSchema(connection: Connection) {
        connection.tx {
            execute(
                """
                BEGIN TRANSACTION;
                CREATE TABLE IF NOT EXISTS store (
                id INTEGER NOT NULL,
                data VARCHAR(255) NOT NULL, 
                PRIMARY KEY (id)
                );
                
                CREATE TABLE IF NOT EXISTS queue (
                id INTEGER NOT NULL,
                PRIMARY KEY (id)
                );
                
                COMMIT;
                """.trimIndent()
            )
        }
    }

    override fun clear() {
        connection.tx {
            execute(
                """
                BEGIN TRANSACTION;
                DELETE FROM store;
                DELETE FROM queue;
                COMMIT;
                """.trimIndent()
            )
        }
    }

    override fun queue(req: Requested) {
        connection.executeQuery<Unit>(
            """
                BEGIN TRANSACTION;
                INSERT OR REPLACE INTO queue (id) VALUES (:id);
                INSERT OR REPLACE INTO store (id, data) VALUES (:id, :data);
                COMMIT;
            """.trimIndent()
        ) {
            query {
                set("id", req.id)
                set("data", json.serialize(req))
            }
        }
    }

    override fun next(limit: Int): List<Requested> {
        val reqIds = ids(
            connection.executeQuery<RequestId>(
                """
            SELECT 
                id 
            FROM 
                queue 
            ORDER BY id DESC
            LIMIT $limit;
            """.trimIndent()
            ) {
                map { rs ->
                    (RequestId(rs.getInt("id")))
                }
            }
        )

        connection.tx {
            execute(" DELETE FROM queue WHERE $reqIds;")
        }


        return connection.executeQuery<Requested>(
            "SELECT data FROM store WHERE $reqIds"
        ) {
            map { rs -> json.decompressAndDeserialize(Requested::class, rs.getBytes("data")) }
        }

    }

    override fun complete(reqId: RequestId) {
        val req = get(reqId)
        check(req.status == RequestStatus.Submitted) { "Req not submitted" }
        connection.executeUpdate(
            "UPDATE store SET data = (:data) WHERE id = (:id);"
        ) {
            set("data", json.serialize(req.apply { status = RequestStatus.Completed }))
            set("id", reqId)
        }
    }

    override fun fail(reqId: RequestId) {
        val req = get(reqId)
        check(req.status == RequestStatus.Submitted) { "Req not submitted" }
        connection.executeUpdate(
            "UPDATE store SET data = (:data) WHERE id = (:id);"
        ) {
            set("data", json.serialize(req.apply { status = RequestStatus.Failed }))
            set("id", reqId)
        }
    }

    override fun close() {

    }

    override fun find(reqId: RequestId): Requested? {
        return connection.executeQueryOne(
            "SELECT * FROM store WHERE id = :id;"
        ) {
            query {
                set("id", reqId)
            }
            map { rs ->
                json.decompressAndDeserialize(Requested::class, rs.getBytes("data"))
            }
        }
    }

    override fun list(query: ReqQuery): List<Requested> {
        return connection.executeQuery<Requested>(
            """
                SELECT
                    data
                FROM
                    store
                WHERE
                    id < :afterId
                ORDER BY id DESC
                LIMIT :limit  
            """.trimIndent()
        ) {
            query {
                set("afterId", query.afterId)
                set("limit", query.limit)
            }
            map { rs ->
                json.decompressAndDeserialize(Requested::class, rs.getBytes("data"))
            }
        }
    }

    override fun count(query: ReqQuery): Count {
        return Count(connection.executeQueryOne(
            """
                SELECT
                    COUNT(*) as count
                FROM
                    store
                WHERE
                    id < :afterId
                ORDER BY id DESC
                LIMIT :limit  
            """.trimIndent()
        ) {
            query {
                set("afterId", query.afterId)
                set("limit", query.limit)
            }
            map {
                it.getLong("count")
            }
        } ?: 0L
        )
    }

    private fun ids(reqIds: List<RequestId>): String {
        return if (reqIds.isEmpty()) {
            ""
        } else {
            "AND id IN (${reqIds.joinToString(",") { "${it.value.value}" }})"
        }
    }


}