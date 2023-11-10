package io.hamal.repository.sqlite

import io.hamal.lib.domain.vo.*
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.NamedResultSet
import io.hamal.lib.sqlite.SqliteBaseRepository
import io.hamal.repository.api.Auth
import io.hamal.repository.api.AuthCmdRepository.*
import io.hamal.repository.api.AuthQueryRepository.AuthQuery
import io.hamal.repository.api.AuthRepository
import io.hamal.repository.api.PasswordAuth
import io.hamal.repository.api.TokenAuth
import java.nio.file.Path

class SqliteAuthRepository(
    config: Config
) : SqliteBaseRepository(config), AuthRepository {
    data class Config(
        override val path: Path
    ) : SqliteBaseRepository.Config {
        override val filename = "auth.db"
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
                CREATE TABLE IF NOT EXISTS auth (
                    cmd_id INTEGER NOT NULL,
                    id INTEGER NOT NULL,
                    type INTEGER NOT NULL,
                    account_id INTEGER NOT NULL,
                    data VARCHAR(255) NOT NULL,
                    expires_at INTEGER,
                    PRIMARY KEY (id)
               )
            """.trimIndent()
            )
        }
    }

    override fun create(cmd: CreateCmd): Auth {
        return when (cmd) {
            is CreatePasswordAuthCmd -> {
                connection.execute<Auth>(
                    """
            INSERT OR REPLACE INTO auth (cmd_id, id, type, account_id, data)
                VALUES(:cmdId, :id, 1, :accountId, :hash) RETURNING *
        """.trimIndent()
                ) {
                    query {
                        set("cmdId", cmd.id)
                        set("id", cmd.authId)
                        set("accountId", cmd.accountId)
                        set("hash", cmd.hash.value)
                    }
                    map(NamedResultSet::toAuth)
                }!!
            }

            is CreateTokenAuthCmd -> {
                connection.execute<Auth>(
                    """
            INSERT OR REPLACE INTO auth (cmd_id, id, type, account_id, data, expires_at)
                VALUES(:cmdId, :id, 2, :accountId, :token, :expiresAt) RETURNING *
        """.trimIndent()
                ) {
                    query {
                        set("cmdId", cmd.id)
                        set("id", cmd.authId)
                        set("accountId", cmd.accountId)
                        set("token", cmd.token.value)
                        set("expiresAt", cmd.expiresAt.value)
                    }
                    map(NamedResultSet::toAuth)
                }!!
            }
        }
    }

    override fun revokeAuth(cmd: UpdateCmd) {
        TODO("Not yet implemented")
    }

    override fun list(query: AuthQuery): List<Auth> {
        return connection.executeQuery<Auth>(
            """
            SELECT 
                *
             FROM
                auth
            WHERE
                id < :afterId
                ${query.ids()}
                ${query.accountIds()}   
            ORDER BY id DESC
            LIMIT :limit
        """.trimIndent()
        ) {
            query {
                set("afterId", query.afterId)
                set("limit", query.limit)
            }
            map(NamedResultSet::toAuth)
        }
    }

    override fun find(authToken: AuthToken): Auth? {
        return connection.executeQueryOne(
            """
            SELECT 
                *
             FROM
                auth
            WHERE
                type = 2 AND 
                data = :token
        """.trimIndent()
        ) {
            query {
                set("token", authToken.value)
            }
            map(NamedResultSet::toAuth)
        }
    }

    override fun count(query: AuthQuery): ULong {
        return connection.executeQueryOne(
            """
            SELECT 
                COUNT(*) as count 
            FROM 
                auth
            WHERE
                id < :afterId
                ${query.ids()}
                ${query.accountIds()}
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

    override fun clear() {
        connection.tx {
            execute("DELETE FROM auth")
        }
    }

    override fun close() {

    }
}

private fun NamedResultSet.toAuth(): Auth {
    return when (getInt("type")) {
        1 -> {
            PasswordAuth(
                cmdId = getCommandId("cmd_id"),
                id = getDomainId("id", ::AuthId),
                accountId = getDomainId("account_id", ::AccountId),
                hash = PasswordHash(getString("data")),
            )
        }

        2 -> {
            TokenAuth(
                cmdId = getCommandId("cmd_id"),
                id = getDomainId("id", ::AuthId),
                accountId = getDomainId("account_id", ::AccountId),
                token = AuthToken(getString("data")),
                expiresAt = AuthTokenExpiresAt(getInstant("expires_at"))
            )
        }

        else -> TODO()
    }
}

private fun AuthQuery.ids(): String {
    return if (authIds.isEmpty()) {
        ""
    } else {
        "AND id IN (${authIds.joinToString(",") { "${it.value.value}" }})"
    }
}

private fun AuthQuery.accountIds(): String {
    return if (accountIds.isEmpty()) {
        ""
    } else {
        "AND account_id IN (${accountIds.joinToString(",") { "${it.value.value}" }})"
    }
}