package io.hamal.repository.sqlite

import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain.vo.*
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.NamedResultSet
import io.hamal.lib.sqlite.SqliteBaseRepository
import io.hamal.repository.api.*
import io.hamal.repository.api.AuthCmdRepository.*
import io.hamal.repository.api.AuthQueryRepository.AuthQuery
import java.nio.file.Path

class AuthSqliteRepository(
    path: Path
) : SqliteBaseRepository(
    path = path,
    filename = "auth.db"
), AuthRepository {

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
                    token VARCHAR(255),
                    email VARCHAR(255),
                    password VARCHAR(255),
                    address VARCHAR(255),
                    expires_at INTEGER,
                    PRIMARY KEY (id)
               )
            """.trimIndent()
            )
        }
    }

    override fun create(cmd: CreateCmd): Auth {
        return when (cmd) {
            is CreateEmailAuthCmd -> {
                connection.execute<Auth>(
                    """
            INSERT OR REPLACE INTO auth (cmd_id, id, type, account_id, email,  password)
                VALUES(:cmdId, :id, 1, :accountId, :email, :password) RETURNING *
        """.trimIndent()
                ) {
                    query {
                        set("cmdId", cmd.id)
                        set("id", cmd.authId)
                        set("accountId", cmd.accountId)
                        set("email", cmd.email.value)
                        set("password", cmd.hash.value)
                    }
                    map(NamedResultSet::toAuth)
                }!!
            }

            is CreateMetaMaskAuthCmd -> {
                connection.execute<Auth>(
                    """
            INSERT OR REPLACE INTO auth (cmd_id, id, type, account_id, address)
                VALUES(:cmdId, :id, 3, :accountId, :address) RETURNING *
        """.trimIndent()
                ) {
                    query {
                        set("cmdId", cmd.id)
                        set("id", cmd.authId)
                        set("accountId", cmd.accountId)
                        set("address", cmd.address.value)
                    }
                    map(NamedResultSet::toAuth)
                }!!
            }

            is CreateTokenAuthCmd -> {
                connection.execute<Auth>(
                    """
            INSERT OR REPLACE INTO auth (cmd_id, id, type, account_id, token, expires_at)
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

    override fun revokeAuth(cmd: RevokeAuthCmd) {
        connection.execute("DELETE FROM auth WHERE id = :id") {
            set("id", cmd.authId)
        }
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
                token = :token
        """.trimIndent()
        ) {
            query {
                set("token", authToken.value)
            }
            map(NamedResultSet::toAuth)
        }
    }

    override fun find(email: Email): Auth? {
        return connection.executeQueryOne(
            """
            SELECT 
                *
             FROM
                auth
            WHERE
                type = 1 AND 
                email = :email
        """.trimIndent()
        ) {
            query {
                set("email", email.value)
            }
            map(NamedResultSet::toAuth)
        }
    }

    override fun find(address: Web3Address): Auth? {
        return connection.executeQueryOne(
            """
            SELECT 
                *
             FROM
                auth
            WHERE
                type = 3 AND 
                address = :address
        """.trimIndent()
        ) {
            query {
                set("address", address.value)
            }
            map(NamedResultSet::toAuth)
        }
    }

    override fun count(query: AuthQuery): Count {
        return Count(
            connection.executeQueryOne(
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
                    it.getLong("count")
                }
            } ?: 0L
        )
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
            EmailAuth(
                cmdId = getCommandId("cmd_id"),
                id = getId("id", ::AuthId),
                accountId = getId("account_id", ::AccountId),
                email = Email(getString("email")),
                hash = PasswordHash(getString("password")),
            )
        }

        2 -> {
            TokenAuth(
                cmdId = getCommandId("cmd_id"),
                id = getId("id", ::AuthId),
                accountId = getId("account_id", ::AccountId),
                token = AuthToken(getString("token")),
                expiresAt = AuthTokenExpiresAt(getInstant("expires_at"))
            )
        }

        3 -> {
            MetaMaskAuth(
                cmdId = getCommandId("cmd_id"),
                id = getId("id", ::AuthId),
                accountId = getId("account_id", ::AccountId),
                address = Web3Address(getString("address"))
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