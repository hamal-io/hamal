package io.hamal.repository.api

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.DomainObject
import io.hamal.lib.domain.vo.AccountEmail
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.AccountName
import kotlinx.serialization.Serializable

@Serializable
data class Account(
    override val id: AccountId,
    val cmdId: CmdId,
    val name: AccountName,
    val email: AccountEmail?
) : DomainObject<AccountId>

interface AccountRepository : AccountCmdRepository, AccountQueryRepository

interface AccountCmdRepository {

    fun create(cmd: CreateCmd): Account

    fun clear()

    data class CreateCmd(
        val id: CmdId,
        val accountId: AccountId,
        val name: AccountName,
        val email: AccountEmail?
    )
}

interface AccountQueryRepository {
    fun find(accountId: AccountId)
}

