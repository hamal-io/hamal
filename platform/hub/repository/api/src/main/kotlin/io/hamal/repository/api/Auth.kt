package io.hamal.repository.api

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.AuthId
import io.hamal.lib.domain.vo.PasswordHash
import io.hamal.lib.domain.vo.PasswordSalt

sealed interface Auth {
    val id: AuthId
    val accountId: AccountId
}

data class PasswordAuth(
    override val id: AuthId,
    val cmdId: CmdId,
    override val accountId: AccountId,
    val hash: PasswordHash,
    val salt: PasswordSalt
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
        val hash: PasswordHash,
        val salt: PasswordSalt
    ) : CreateCmd
}


interface AuthQueryRepository
