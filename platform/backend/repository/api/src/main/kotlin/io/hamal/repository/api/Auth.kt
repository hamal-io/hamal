package io.hamal.repository.api

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Count
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.vo.*

sealed interface Auth {
    val id: AuthId

    object Anonymous : Auth {
        override val id: AuthId get() = AuthId.anonymous
        override fun toString(): String = javaClass.simpleName
    }

    object System : Auth {
        override val id: AuthId get() = AuthId.system
        override fun toString(): String = javaClass.simpleName
    }

    sealed interface Account : Auth, HasAccountId {
        override val accountId: AccountId
    }

    data class Email(
        override val id: AuthId,
        val cmdId: CmdId,
        override val accountId: AccountId,
        val email: io.hamal.lib.domain.vo.Email,
        val hash: PasswordHash
    ) : Account

    data class MetaMask(
        override val id: AuthId,
        val cmdId: CmdId,
        override val accountId: AccountId,
        val address: Web3Address
    ) : Account

    data class Token(
        override val id: AuthId,
        val cmdId: CmdId,
        override val accountId: AccountId,
        val token: AuthToken,
        val expiresAt: AuthTokenExpiresAt
    ) : Account

    data class Runner(
        override val id: AuthId,
        val cmdId: CmdId,
        override val accountId: AccountId,
        val token: AuthToken,
        val execToken: ExecToken
    ) : Account
}

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
    fun get(authId: AuthId) = find(authId) ?: throw NoSuchElementException("Auth not found")
    fun find(authId: AuthId): Auth?
    fun get(authToken: AuthToken) = find(authToken) ?: throw NoSuchElementException("Auth not found")
    fun find(authToken: AuthToken): Auth?
    fun get(email: Email) = find(email) ?: throw NoSuchElementException("Auth not found")
    fun find(email: Email): Auth?
    fun get(address: Web3Address) = find(address) ?: throw NoSuchElementException("Auth not found")
    fun find(address: Web3Address): Auth?

    fun list(accountId: AccountId) = list(
        AuthQuery(
            limit = Limit.all,
            accountIds = listOf(accountId)
        )
    )

    fun list(query: AuthQuery): List<Auth>
    fun count(query: AuthQuery): Count

    data class AuthQuery(
        var afterId: AuthId = AuthId(SnowflakeId(Long.MAX_VALUE)),
        var limit: Limit = Limit(1),
        var authIds: List<AuthId> = listOf(),
        var accountIds: List<AccountId> = listOf()
    )
}
