package io.hamal.repository.sqlite

import io.hamal.lib.common.domain.Count
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.Requested
import io.hamal.lib.domain.vo.RequestId
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.SqliteBaseRepository
import io.hamal.repository.api.RequestQueryRepository.RequestQuery
import io.hamal.repository.api.RequestRepository
import io.hamal.repository.record.json
import java.nio.file.Path

class RequestSqliteRepository(
    config: Config
) : SqliteBaseRepository(config), RequestRepository {

    data class Config(
        override val path: Path
    ) : SqliteBaseRepository.Config {
        override val filename = "requests.db"
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
                CREATE TABLE IF NOT EXISTS requests (
                    id INTEGER PRIMARY KEY,
                    status INTEGER NOT NULL,
                    data BLOB NOT NULL 
                )
                """.trimIndent()
            )
        }
    }

    override fun clear() {
        connection.tx { execute("DELETE FROM requests") }
    }

    override fun queue(req: Requested) {
        connection.tx {
            execute("INSERT INTO requests (id,status,data) VALUES (:id,:status,:data)") {
                set("id", req.id)
                set("status", req.status.value)
                set("data", json.serialize(req))
            }
        }
    }


    override fun next(limit: Limit): List<Requested> {
        return connection.tx {
            executeQuery(
                """
                UPDATE requests
                    SET
                        status = 2
                    WHERE id IN (
                        SELECT 
                            id
                         FROM requests
                         WHERE
                             status = 1
                         LIMIT :limit
                    )
                RETURNING status,data
            """.trimIndent()
            ) {
                query {
                    set("limit", limit)
                }
                map { rs ->
                    json.decompressAndDeserialize(Requested::class, rs.getBytes("data")).apply {
                        status = RequestStatus.fromInt(rs.getInt("status"))
                    }
                }
            }
        }
    }

    override fun complete(reqId: RequestId) {
        connection.tx {
            executeQueryOne<RequestId>(
                "UPDATE requests SET status = 3 WHERE id = (:id) AND status = 2 RETURNING id"
            ) {
                query {
                    set("id", reqId)
                }
                map { rs ->
                    rs.getId("id", ::RequestId)
                }
            }
        } ?: run {
            get(reqId)
            throw IllegalStateException("Request not processing")
        }
    }

    override fun fail(reqId: RequestId) {
        connection.tx {
            executeQueryOne<RequestId>(
                "UPDATE requests SET status = 4 WHERE id = (:id) AND status = 2 RETURNING id"
            ) {
                query {
                    set("id", reqId)
                }
                map { rs ->
                    rs.getId("id", ::RequestId)
                }
            }
        } ?: run {
            get(reqId)
            throw IllegalStateException("Request not processing")
        }
    }

    override fun find(reqId: RequestId): Requested? {
        return connection.executeQueryOne(
            "SELECT status,data FROM requests WHERE id = :id"
        ) {
            query {
                set("id", reqId)
            }
            map { rs ->
                json.decompressAndDeserialize(Requested::class, rs.getBytes("data")).apply {
                    status = RequestStatus.fromInt(rs.getInt("status"))
                }
            }
        }
    }

    override fun list(query: RequestQuery): List<Requested> {
        return connection.executeQuery<Requested>(
            """
            SELECT
                status,
                data
            FROM
                requests
            WHERE
                id > :afterId
            ORDER BY id DESC
            LIMIT :limit
            """.trimIndent()
        ) {
            query {
                set("afterId", query.afterId)
                set("limit", query.limit)
            }
            map { rs ->
                json.decompressAndDeserialize(Requested::class, rs.getBytes("data")).apply {
                    status = RequestStatus.fromInt(rs.getInt("status"))
                }
            }
        }
    }

    override fun count(query: RequestQuery): Count {
        return Count(connection.executeQueryOne(
            """
            SELECT 
                COUNT(*) as count 
            FROM 
                requests
            WHERE
                id > :afterId
            """.trimIndent()
        ) {
            query {
                set("afterId", query.afterId)
            }
            map {
                it.getLong("count")
            }
        } ?: 0L)
    }
}