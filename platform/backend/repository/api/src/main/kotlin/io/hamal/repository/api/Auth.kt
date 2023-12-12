package io.hamal.repository.api

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.vo.*

sealed interface Auth {
    val id: AuthId
    val accountId: AccountId
}

data class EmailAuth(
    override val id: AuthId,
    val cmdId: CmdId,
    override val accountId: AccountId,
    val email: Email,
    val hash: PasswordHash
) : Auth

data class MetaMaskAuth(
    override val id: AuthId,
    val cmdId: CmdId,
    override val accountId: AccountId,
    val address: Web3Address
) : Auth

data class TokenAuth(
    override val id: AuthId,
    val cmdId: CmdId,
    override val accountId: AccountId,
    val token: AuthToken,
    val expiresAt: AuthTokenExpiresAt
) : Auth


interface AuthRepository : AuthCmdRepository, AuthQueryRepository

interface AuthCmdRepository : CmdRepository {

    fun create(cmd: CreateCmd): Auth

    fun revokeAuth(cmd: RevokeAuthCmd)

    sealed interface CreateCmd {
        val id: CmdId
        val authId: AuthId
        val accountId: AccountId
    }

    data class RevokeAuthCmd(
        val id: CmdId,
        val authId: AuthId
    )

    data class CreateEmailAuthCmd(
        override val id: CmdId,
        override val authId: AuthId,
        override val accountId: AccountId,
        val email: Email,
        val hash: PasswordHash
    ) : CreateCmd

    data class CreateMetaMaskAuthCmd(
        override val id: CmdId,
        override val authId: AuthId,
        override val accountId: AccountId,
        val address: Web3Address
    ) : CreateCmd

    data class CreateTokenAuthCmd(
        override val id: CmdId,
        override val authId: AuthId,
        override val accountId: AccountId,
        val token: AuthToken,
        val expiresAt: AuthTokenExpiresAt
    ) : CreateCmd
}


interface AuthQueryRepository {
    fun get(authToken: AuthToken) = find(authToken) ?: throw NoSuchElementException("Auth not found")
    fun find(authToken: AuthToken): Auth?
    fun get(email: Email) = find(email) ?: throw NoSuchElementException("Auth not found")
    fun find(email: Email): Auth?

    fun list(accountId: AccountId) = list(
        AuthQuery(
            limit = Limit.all,
            accountIds = listOf(accountId)
        )
    )

    fun list(query: AuthQuery): List<Auth>
    fun count(query: AuthQuery): ULong

    data class AuthQuery(
        var afterId: AuthId = AuthId(SnowflakeId(Long.MAX_VALUE)),
        var limit: Limit = Limit(1),
        var authIds: List<AuthId> = listOf(),
        var accountIds: List<AccountId> = listOf()
    )
}
