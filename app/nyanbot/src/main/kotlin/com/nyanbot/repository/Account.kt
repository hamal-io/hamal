package com.nyanbot.repository

import io.hamal.lib.common.domain.Count
import io.hamal.lib.common.domain.DomainObject
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.domain.UpdatedAt
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.AccountType
import io.hamal.lib.domain.vo.PasswordSalt

interface HasAccountId {
    val accountId: AccountId
}

data class Account(
    override val id: AccountId,
    override val updatedAt: UpdatedAt,
    val type: AccountType,
    val salt: PasswordSalt
) : DomainObject<AccountId>, HasAccountId {
    override val accountId: AccountId get() = id
}

interface AccountRepository : AccountCmdRepository, AccountQueryRepository

interface AccountCmdRepository {

    fun create(cmd: CreateCmd): Account

    data class CreateCmd(
        val accountId: AccountId,
        val accountType: AccountType,
        val salt: PasswordSalt
    )
}

interface AccountQueryRepository {
    fun get(accountId: AccountId) = find(accountId) ?: throw NoSuchElementException("Account not found")
    fun find(accountId: AccountId): Account?
    fun list(query: AccountQuery): List<Account>
    fun list(accountIds: List<AccountId>): List<Account> = list(
        AccountQuery(
            limit = Limit.all,
            accountIds = accountIds,
        )
    )

    fun count(query: AccountQuery): Count

    data class AccountQuery(
        var afterId: AccountId = AccountId(SnowflakeId(Long.MAX_VALUE)),
        var limit: Limit = Limit(1),
        var accountIds: List<AccountId> = listOf()
    )
}
