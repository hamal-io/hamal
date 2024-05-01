package io.hamal.repository

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.CmdId.Companion.CmdId
import io.hamal.lib.common.domain.Count
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.AccountId.Companion.AccountId
import io.hamal.lib.domain.vo.AccountType.Root
import io.hamal.lib.domain.vo.AccountType.User
import io.hamal.lib.domain.vo.PasswordSalt
import io.hamal.lib.domain.vo.PasswordSalt.Companion.PasswordSalt
import io.hamal.repository.api.AccountCmdRepository.CreateCmd
import io.hamal.repository.api.AccountQueryRepository.AccountQuery
import io.hamal.repository.api.AccountRepository
import io.hamal.repository.fixture.AbstractUnitTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows
import kotlin.math.abs
import kotlin.random.Random

internal class AccountRepositoryTest : AbstractUnitTest() {
    @Nested
    inner class CreateTest {
        @TestFactory
        fun `Creates account`() = runWith(AccountRepository::class) {
            val result = create(
                CreateCmd(
                    id = CmdId(1),
                    accountId = AccountId(123),
                    accountType = Root,
                    salt = PasswordSalt("SALT")
                )
            )

            with(result) {
                assertThat(id, equalTo(AccountId(123)))
                assertThat(type, equalTo(Root))
                assertThat(salt, equalTo(PasswordSalt("SALT")))
            }

            verifyCount(1)
        }

        @TestFactory
        fun `Creates account without email`() = runWith(AccountRepository::class) {
            val result = create(
                CreateCmd(
                    id = CmdId(1),
                    accountId = AccountId(123),
                    accountType = Root,
                    salt = PasswordSalt("SALT")
                )
            )

            with(result) {
                assertThat(id, equalTo(AccountId(123)))
                assertThat(type, equalTo(Root))
                assertThat(salt, equalTo(PasswordSalt("SALT")))
            }

            verifyCount(1)
        }
    }

    @Nested
    inner class ClearTest {

        @TestFactory
        fun `Nothing to clear`() = runWith(AccountRepository::class) {
            clear()
            verifyCount(0)
        }

        @TestFactory
        fun `Clear table`() = runWith(AccountRepository::class) {
            createAccount(
                accountId = AccountId(1),
                salt = PasswordSalt("salt")
            )

            createAccount(
                accountId = AccountId(2),
                salt = PasswordSalt("salt")
            )

            clear()
            verifyCount(0)
        }

    }

    @Nested
    inner class GetTest {
        @TestFactory
        fun `Get account by id`() = runWith(AccountRepository::class) {
            createAccount(
                accountId = AccountId(1),
                salt = PasswordSalt("salt")
            )

            with(get(AccountId(1))) {
                assertThat(id, equalTo(AccountId(1)))
                assertThat(salt, equalTo(PasswordSalt("salt")))
            }
        }

        @TestFactory
        fun `Tries to get account by id but does not exist`() = runWith(AccountRepository::class) {
            createAccount(
                accountId = AccountId(1),
                salt = PasswordSalt("salt")
            )

            val exception = assertThrows<NoSuchElementException> {
                get(AccountId(111111))
            }
            assertThat(exception.message, equalTo("Account not found"))
        }
    }

    @Nested
    inner class FindTest {

        @TestFactory
        fun `Find account by id`() = runWith(AccountRepository::class) {
            createAccount(
                accountId = AccountId(1),
                salt = PasswordSalt("salt")
            )

            with(find(AccountId(1))!!) {
                assertThat(id, equalTo(AccountId(1)))
                assertThat(salt, equalTo(PasswordSalt("salt")))
            }
        }

        @TestFactory
        fun `Tries to find account by id but does not exist`() = runWith(AccountRepository::class) {
            createAccount(
                accountId = AccountId(1),
                salt = PasswordSalt("salt")
            )

            val result = find(AccountId(111111))
            assertThat(result, nullValue())
        }
    }

    @Nested
    inner class ListAndCountTest {

        @TestFactory
        fun `By ids`() = runWith(AccountRepository::class) {
            setup()

            val result = list(listOf(AccountId(111111), AccountId(3)))
            assertThat(result, hasSize(1))

            with(result[0]) {
                assertThat(id, equalTo(AccountId(3)))
            }
        }

        @TestFactory
        fun `Limit`() = runWith(AccountRepository::class) {
            setup()

            val query = AccountQuery(
                limit = Limit(3),
                accountIds = listOf()
            )

            assertThat(count(query), equalTo(Count(4)))
            val result = list(query)
            assertThat(result, hasSize(3))
        }

        @TestFactory
        fun `Skip and limit`() = runWith(AccountRepository::class) {
            setup()

            val query = AccountQuery(
                afterId = AccountId(2),
                limit = Limit(1)
            )

            assertThat(count(query), equalTo(Count(1)))
            val result = list(query)
            assertThat(result, hasSize(1))

            with(result[0]) {
                assertThat(id, equalTo(AccountId(1)))
            }
        }

        private fun AccountRepository.setup() {
            createAccount(
                accountId = AccountId(1),
                salt = PasswordSalt("salt")
            )

            createAccount(
                accountId = AccountId(2),
                salt = PasswordSalt("salt")
            )

            createAccount(
                accountId = AccountId(3),
                salt = PasswordSalt("salt")
            )

            createAccount(
                accountId = AccountId(4),
                salt = PasswordSalt("salt")
            )
        }
    }
}

private fun AccountRepository.createAccount(
    accountId: AccountId,
    salt: PasswordSalt,
    cmdId: CmdId = CmdId(abs(Random(10).nextInt()) + 10)
) {
    create(
        CreateCmd(
            id = cmdId,
            accountId = accountId,
            accountType = User,
            salt = salt
        )
    )
}

private fun AccountRepository.verifyCount(expected: Int) {
    verifyCount(expected) { }
}

private fun AccountRepository.verifyCount(expected: Int, block: AccountQuery.() -> Unit) {
    val counted = count(AccountQuery().also(block))
    assertThat("number of accounts expected", counted, equalTo(Count(expected)))
}