package io.hamal.repository.sqlite

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain.vo.*
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.NamedResultSet
import io.hamal.lib.sqlite.SqliteBaseRepository
import io.hamal.repository.api.Auth
import io.hamal.repository.api.AuthCmdRepository.*
import io.hamal.repository.api.AuthQueryRepository.AuthQuery
import io.hamal.repository.api.AuthRepository
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
                    entity_id INTEGER,
                    token VARCHAR(255),
                    email VARCHAR(255),
                    password VARCHAR(255),
                    address VARCHAR(255),
                    expires_at INTEGER,
                    PRIMARY KEY (id)
               );
            """.trimIndent()
            )
        }
    }

    override fun create(cmd: CreateCmd): Auth {
        return when (cmd) {
            is CreateEmailAuthCmd -> {
                connection.execute<Auth>(
                    """
            INSERT OR REPLACE INTO auth (cmd_id, id, type, entity_id, email,  password)
                VALUES(:cmdId, :id, 1, :entityId, :email, :password) RETURNING *
        """.trimIndent()
                ) {
                    query {
                        set("cmdId", cmd.id)
                        set("id", cmd.authId)
                        set("entityId", cmd.accountId)
                        set("email", cmd.email.value)
                        set("password", cmd.hash.value)
                    }
                    map(NamedResultSet::toAuth)
                }!!
            }

            is CreateMetaMaskAuthCmd -> {
                connection.execute<Auth>(
                    """
            INSERT OR REPLACE INTO auth (cmd_id, id, type, entity_id, address)
                VALUES(:cmdId, :id, 3, :entityId, :address) RETURNING *
        """.trimIndent()
                ) {
                    query {
                        set("cmdId", cmd.id)
                        set("id", cmd.authId)
                        set("entityId", cmd.accountId)
                        set("address", cmd.address.value)
                    }
                    map(NamedResultSet::toAuth)
                }!!
            }

            is CreateTokenAuthCmd -> {
                connection.execute<Auth>(
                    """
            INSERT OR REPLACE INTO auth (cmd_id, id, type, entity_id, token, expires_at)
                VALUES(:cmdId, :id, 2, :entityId, :token, :expiresAt) RETURNING *
        """.trimIndent()
                ) {
                    query {
                        set("cmdId", cmd.id)
                        set("id", cmd.authId)
                        set("entityId", cmd.accountId)
                        set("token", cmd.token)
                        set("expiresAt", cmd.expiresAt.value)
                    }
                    map(NamedResultSet::toAuth)
                }!!
            }

            is CreateExecTokenAuthCmd -> {
                connection.execute<Auth>(
                    """
            INSERT OR REPLACE INTO auth (cmd_id, id, type, entity_id, token)
                VALUES(:cmdId, :id, 4, :entityId, :token) RETURNING *
        """.trimIndent()
                ) {
                    query {
                        set("cmdId", cmd.id)
                        set("id", cmd.authId)
                        set("entityId", cmd.execId)
                        set("token", cmd.token)
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
                set("token", authToken)
            }
            map(NamedResultSet::toAuth)
        }
    }

    override fun find(execToken: ExecToken): Auth.ExecToken? {
        return connection.executeQueryOne(
            """
            SELECT 
                *
             FROM
                auth
            WHERE
                type = 4 AND 
                token = :token
        """.trimIndent()
        ) {
            query {
                set("token", execToken)
            }
            map(NamedResultSet::toAuth)
        }?.let { it as Auth.ExecToken }
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

    override fun find(authId: AuthId): Auth? {
        return connection.executeQueryOne(
            """
            SELECT 
                *
             FROM
                auth
            WHERE
                id = :id
        """.trimIndent()
        ) {
            query {
                set("id", authId)
            }
            map(NamedResultSet::toAuth)
        }
    }

    override fun find(execId: ExecId): Auth.ExecToken? {
        return connection.executeQueryOne(
            """
            SELECT 
                *
             FROM
                auth
            WHERE
                type = 4 AND 
                entity_id = :entityId
        """.trimIndent()
        ) {
            query {
                set("entityId", execId)
            }
            map(NamedResultSet::toAuth)
        }?.let { it as Auth.ExecToken }
    }

    override fun update(authId: AuthId, cmd: UpdatePasswordCmd): Auth {
        return connection.execute<Auth>(
            """
            UPDATE 
                auth 
            SET 
                password = :password
            WHERE
                id = :id
            RETURNING *;
        """.trimIndent()
        ) {
            query {
                set("id", authId)
                set("password", cmd.hash)
            }
            map(NamedResultSet::toAuth)
        }!!
    }
}

private fun NamedResultSet.toAuth(): Auth {
    return when (getInt("type")) {
        1 -> {
            Auth.Email(
                cmdId = getId("cmd_id", ::CmdId),
                id = getId("id", ::AuthId),
                accountId = getId("entity_id", ::AccountId),
                email = Email(getString("email")),
                hash = PasswordHash(getString("password")),
            )
        }

        2 -> {
            Auth.Token(
                cmdId = getId("cmd_id", ::CmdId),
                id = getId("id", ::AuthId),
                accountId = getId("entity_id", ::AccountId),
                token = AuthToken(getString("token")),
                expiresAt = ExpiresAt(getInstant("expires_at"))
            )
        }

        3 -> {
            Auth.MetaMask(
                cmdId = getId("cmd_id", ::CmdId),
                id = getId("id", ::AuthId),
                accountId = getId("entity_id", ::AccountId),
                address = Web3Address(getString("address"))
            )
        }

        4 -> {
            Auth.ExecToken(
                cmdId = getId("cmd_id", ::CmdId),
                id = getId("id", ::AuthId),
                execId = getId("entity_id", ::ExecId),
                token = ExecToken(getString("token"))
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
        "AND entity_id IN (${accountIds.joinToString(",") { "${it.value.value}" }})"
    }
}