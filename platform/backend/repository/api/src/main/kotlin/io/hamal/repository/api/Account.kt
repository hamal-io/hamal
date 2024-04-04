package io.hamal.repository.api

import io.hamal.lib.common.domain.*
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.AccountType
import io.hamal.lib.domain.vo.Email
import io.hamal.lib.domain.vo.PasswordSalt

data class Account(
    override val id: AccountId,
    override val updatedAt: UpdatedAt,
    val cmdId: CmdId,
    val type: AccountType,
    val salt: PasswordSalt
) : DomainObject<AccountId>, HasAccountId {
    override val accountId: AccountId get() = id
}

interface AccountRepository : AccountCmdRepository, AccountQueryRepository

interface AccountCmdRepository : CmdRepository {

    fun create(cmd: CreateCmd): Account

    fun convert(cmd: ConvertCmd): Account

    fun update(accountId: AccountId, cmd: UpdateCmd) : Account

    data class CreateCmd(
        val id: CmdId,
        val accountId: AccountId,
        val accountType: AccountType,
        val salt: PasswordSalt
    )

    data class ConvertCmd(
        val id: CmdId,
        val accountId: AccountId,
        val email: Email
    )

    data class UpdateCmd(
        val id: CmdId,
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
