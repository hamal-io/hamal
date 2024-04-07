package com.nyanbot.repository

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

    data class MetaMask(
        override val id: AuthId,
        override val accountId: AccountId,
        val address: Web3Address
    ) : Auth, HasAccountId

    data class Token(
        override val id: AuthId,
        override val accountId: AccountId,
        val token: AuthToken,
        val expiresAt: ExpiresAt
    ) : Auth, HasAccountId
}

interface AuthRepository : AuthCmdRepository, AuthQueryRepository

interface AuthCmdRepository {

    fun create(cmd: CreateCmd): Auth

    fun revokeAuth(cmd: RevokeAuthCmd)

    sealed interface CreateCmd {
        val authId: AuthId
    }

    data class RevokeAuthCmd(
        val authId: AuthId
    )

    data class CreateMetaMaskAuthCmd(
        override val authId: AuthId,
        val accountId: AccountId,
        val address: Web3Address
    ) : CreateCmd

    data class CreateTokenAuthCmd(
        override val authId: AuthId,
        val accountId: AccountId,
        val token: AuthToken,
        val expiresAt: ExpiresAt
    ) : CreateCmd

}


interface AuthQueryRepository {

    fun get(authId: AuthId) = find(authId) ?: throw NoSuchElementException("Auth not found")
    fun find(authId: AuthId): Auth?

    fun get(authToken: AuthToken) = find(authToken) ?: throw NoSuchElementException("Auth not found")
    fun find(authToken: AuthToken): Auth?

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
