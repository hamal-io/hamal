package io.hamal.repository.api

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.*

sealed interface Auth {
    val id: AuthId
    val accountId: AccountId
}

data class PasswordAuth(
    override val id: AuthId,
    val cmdId: CmdId,
    override val accountId: AccountId,
    val hash: PasswordHash
) : Auth

data class TokenAuth(
    override val id: AuthId,
    val cmdId: CmdId,
    override val accountId: AccountId,
    val token: AuthToken,
    val expiresAt: AuthTokenExpiresAt
) : Auth


interface AuthRepository : AuthCmdRepository, AuthQueryRepository

interface AuthCmdRepository {

    fun create(cmd: CreateCmd): Auth

    fun list(accountId: AccountId): List<Auth>

    fun clear()

    sealed interface CreateCmd {
        val id: CmdId
        val authId: AuthId
        val accountId: AccountId
    }

    data class CreatePasswordAuthCmd(
        override val id: CmdId,
        override val authId: AuthId,
        override val accountId: AccountId,
        val hash: PasswordHash
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
    fun get(authToken: AuthToken) = find(authToken) ?: throw NoSuchElementException("Account not found")
    fun find(authToken: AuthToken): Auth?
    fun list(accountId: AccountId): List<Auth>
}
