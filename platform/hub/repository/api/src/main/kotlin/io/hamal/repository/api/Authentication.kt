package io.hamal.repository.api

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.AccountPassword
import io.hamal.lib.domain.vo.AuthenticationPasswordHash
import io.hamal.lib.domain.vo.AuthenticationId

sealed interface Authentication {
    val id: AuthenticationId
    val accountId: AccountId
}

data class PasswordAuthentication(
    override val id: AuthenticationId,
    val cmdId: CmdId,
    override val accountId: AccountId,
    val passwordHash: AuthenticationPasswordHash
) : Authentication

interface AuthenticationRepository : AuthenticationCmdRepository, AuthenticationQueryRepository

interface AuthenticationCmdRepository {

    fun create(cmd: CreateCmd): Authentication

    fun clear()

    sealed interface CreateCmd {
        val id: CmdId
        val authenticationId: AuthenticationId
        val accountId: AccountId
    }

    data class CreatePasswordAuthenticationCmd(
        override val id: CmdId,
        override val authenticationId: AuthenticationId,
        override val accountId: AccountId,
        val password: AccountPassword
    ) : CreateCmd
}


interface AuthenticationQueryRepository
