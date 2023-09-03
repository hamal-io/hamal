package io.hamal.repository.api

import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.DomainObject
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.AccountEmail
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.AccountName
import io.hamal.lib.domain.vo.PasswordSalt
import kotlinx.serialization.Serializable

@Serializable
data class Account(
    override val id: AccountId,
    val cmdId: CmdId,
    val name: AccountName,
    val email: AccountEmail?,
    val salt: PasswordSalt
) : DomainObject<AccountId>

interface AccountRepository : AccountCmdRepository, AccountQueryRepository

interface AccountCmdRepository {

    fun create(cmd: CreateCmd): Account

    fun clear()

    data class CreateCmd(
        val id: CmdId,
        val accountId: AccountId,
        val name: AccountName,
        val email: AccountEmail?,
        val salt: PasswordSalt
    )
}

interface AccountQueryRepository {
    fun get(accountId: AccountId) = find(accountId) ?: throw NoSuchElementException("Account not found")
    fun find(accountId: AccountId): Account?
    fun get(accountName: AccountName) = find(accountName) ?: throw NoSuchElementException("Account not found")
    fun find(accountName: AccountName): Account?
    fun list(block: AccountQuery.() -> Unit): List<Account>
    fun list(accountIds: List<AccountId>): List<Account> = accountIds.map(::get)
    data class AccountQuery(
        var afterId: AccountId = AccountId(SnowflakeId(Long.MAX_VALUE)),
        var limit: Limit = Limit(1)
    )
}
