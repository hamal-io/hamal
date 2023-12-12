package io.hamal.repository.api

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.DomainObject
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.domain.UpdatedAt
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.vo.*
import kotlinx.serialization.Serializable

@Serializable
data class Account(
    override val id: AccountId,
    override val updatedAt: UpdatedAt,
    val cmdId: CmdId,
    val type: AccountType,
    val name: AccountName,
    val email: AccountEmail?,
    val salt: PasswordSalt
) : DomainObject<AccountId>

interface AccountRepository : AccountCmdRepository, AccountQueryRepository

interface AccountCmdRepository : CmdRepository {

    fun create(cmd: CreateCmd): Account

    fun convert(cmd: ConvertCmd): Account

    data class CreateCmd(
        val id: CmdId,
        val accountId: AccountId,
        val accountType: AccountType,
        val name: AccountName,
        val email: AccountEmail?,
        val salt: PasswordSalt
    )

    data class ConvertCmd(
        val id: CmdId,
        val accountId: AccountId,
        val name: AccountName?,
        val email: AccountEmail?,
    )
}

interface AccountQueryRepository {
    fun get(accountId: AccountId) = find(accountId) ?: throw NoSuchElementException("Account not found")
    fun find(accountId: AccountId): Account?
    fun get(accountName: AccountName) = find(accountName) ?: throw NoSuchElementException("Account not found")
    fun find(accountName: AccountName): Account?
    fun list(query: AccountQuery): List<Account>
    fun list(accountIds: List<AccountId>): List<Account> = list(
        AccountQuery(
            limit = Limit.all,
            accountIds = accountIds,
        )
    )

    fun count(query: AccountQuery): ULong

    data class AccountQuery(
        var afterId: AccountId = AccountId(SnowflakeId(Long.MAX_VALUE)),
        var limit: Limit = Limit(1),
        var accountIds: List<AccountId> = listOf()
    )
}
