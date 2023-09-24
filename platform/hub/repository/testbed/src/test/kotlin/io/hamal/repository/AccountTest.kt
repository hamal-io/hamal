package io.hamal.repository

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.AccountEmail
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.AccountName
import io.hamal.lib.domain.vo.AccountType.Enjoyer
import io.hamal.lib.domain.vo.AccountType.Root
import io.hamal.lib.domain.vo.PasswordSalt
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
                    name = AccountName("SomeAccount"),
                    email = AccountEmail("contact@fn.guru"),
                    salt = PasswordSalt("SALT")
                )
            )

            with(result) {
                assertThat(id, equalTo(AccountId(123)))
                assertThat(type, equalTo(Root))
                assertThat(name, equalTo(AccountName("SomeAccount")))
                assertThat(email, equalTo(AccountEmail("contact@fn.guru")))
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
                    name = AccountName("SomeAccount"),
                    email = null,
                    salt = PasswordSalt("SALT")
                )
            )

            with(result) {
                assertThat(id, equalTo(AccountId(123)))
                assertThat(type, equalTo(Root))
                assertThat(name, equalTo(AccountName("SomeAccount")))
                assertThat(email, nullValue())
                assertThat(salt, equalTo(PasswordSalt("SALT")))
            }

            verifyCount(1)
        }

        @TestFactory
        fun `Tries to create but same name already exist`() =
            runWith(AccountRepository::class) {

                createAccount(
                    accountId = AccountId(1),
                    name = AccountName("first-account-name"),
                    email = AccountEmail("mail@fn.guru"),
                    salt = PasswordSalt("salt")
                )

                val exception = assertThrows<IllegalArgumentException> {
                    create(
                        CreateCmd(
                            id = CmdId(2),
                            accountId = AccountId(2),
                            accountType = Enjoyer,
                            name = AccountName("first-account-name"),
                            email = AccountEmail("another@hamal.io"),
                            salt = PasswordSalt("salt"),
                        )
                    )
                }

                assertThat(
                    exception.message,
                    equalTo("AccountName(first-account-name) already exists")
                )

                verifyCount(1)
            }

        @TestFactory
        fun `Tries to create but same email already exist`() =
            runWith(AccountRepository::class) {

                createAccount(
                    accountId = AccountId(1),
                    name = AccountName("first-account-name"),
                    email = AccountEmail("mail@fn.guru"),
                    salt = PasswordSalt("salt")
                )

                val exception = assertThrows<IllegalArgumentException> {
                    create(
                        CreateCmd(
                            id = CmdId(2),
                            accountId = AccountId(2),
                            accountType = Enjoyer,
                            name = AccountName("second-account-name"),
                            email = AccountEmail("mail@fn.guru"),
                            salt = PasswordSalt("salt"),
                        )
                    )
                }

                assertThat(
                    exception.message,
                    equalTo("AccountEmail(mail@fn.guru) already exists")
                )

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
                name = AccountName("account-name"),
                email = AccountEmail("mail@fn.guru"),
                salt = PasswordSalt("salt")
            )

            createAccount(
                accountId = AccountId(2),
                name = AccountName("another-account-name"),
                email = AccountEmail("another-email@fn.guru"),
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
                name = AccountName("SomeAccount"),
                email = AccountEmail("mail@fn.guru"),
                salt = PasswordSalt("salt")
            )

            with(get(AccountId(1))) {
                assertThat(id, equalTo(AccountId(1)))
                assertThat(name, equalTo(AccountName("SomeAccount")))
                assertThat(email, equalTo(AccountEmail("mail@fn.guru")))
                assertThat(salt, equalTo(PasswordSalt("salt")))
            }
        }

        @TestFactory
        fun `Tries to get account by id but does not exist`() = runWith(AccountRepository::class) {
            createAccount(
                accountId = AccountId(1),
                name = AccountName("SomeAccount"),
                email = AccountEmail("mail@fn.guru"),
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
        fun `Find account by name`() = runWith(AccountRepository::class) {
            createAccount(
                accountId = AccountId(1),
                name = AccountName("SomeAccount"),
                email = AccountEmail("mail@fn.guru"),
                salt = PasswordSalt("salt")
            )

            with(find(AccountName("SomeAccount"))!!) {
                assertThat(id, equalTo(AccountId(1)))
                assertThat(name, equalTo(AccountName("SomeAccount")))
                assertThat(email, equalTo(AccountEmail("mail@fn.guru")))
                assertThat(salt, equalTo(PasswordSalt("salt")))
            }
        }

        @TestFactory
        fun `Tries to find account by name but does not exist`() = runWith(AccountRepository::class) {
            createAccount(
                accountId = AccountId(1),
                name = AccountName("account-name"),
                email = AccountEmail("mail@fn.guru"),
                salt = PasswordSalt("salt")
            )

            val result = find(AccountName("account-name-does-not-exists"))
            assertThat(result, nullValue())
        }

        @TestFactory
        fun `Find account by id`() = runWith(AccountRepository::class) {
            createAccount(
                accountId = AccountId(1),
                name = AccountName("SomeAccount"),
                email = AccountEmail("mail@fn.guru"),
                salt = PasswordSalt("salt")
            )

            with(find(AccountId(1))!!) {
                assertThat(id, equalTo(AccountId(1)))
                assertThat(name, equalTo(AccountName("SomeAccount")))
                assertThat(email, equalTo(AccountEmail("mail@fn.guru")))
                assertThat(salt, equalTo(PasswordSalt("salt")))
            }
        }

        @TestFactory
        fun `Tries to find account by id but does not exist`() = runWith(AccountRepository::class) {
            createAccount(
                accountId = AccountId(1),
                name = AccountName("account-name"),
                email = AccountEmail("mail@fn.guru"),
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
                assertThat(name, equalTo(AccountName("Account-Three")))
                assertThat(email, equalTo(AccountEmail("account@three.com")))
            }
        }

        @TestFactory
        fun `Limit`() = runWith(AccountRepository::class) {
            setup()

            val query = AccountQuery(
                limit = Limit(3),
                accountIds = listOf()
            )

            assertThat(count(query), equalTo(4UL))
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

            assertThat(count(query), equalTo(1UL))
            val result = list(query)
            assertThat(result, hasSize(1))

            with(result[0]) {
                assertThat(id, equalTo(AccountId(1)))
            }
        }

        private fun AccountRepository.setup() {
            createAccount(
                accountId = AccountId(1),
                name = AccountName("Account-One"),
                email = AccountEmail("account@one.com"),
                salt = PasswordSalt("salt")
            )

            createAccount(
                accountId = AccountId(2),
                name = AccountName("Account-Two"),
                email = AccountEmail("account@two.com"),
                salt = PasswordSalt("salt")
            )

            createAccount(
                accountId = AccountId(3),
                name = AccountName("Account-Three"),
                email = AccountEmail("account@three.com"),
                salt = PasswordSalt("salt")
            )

            createAccount(
                accountId = AccountId(4),
                name = AccountName("Account-Four"),
                email = AccountEmail("account@four.com"),
                salt = PasswordSalt("salt")
            )
        }
    }
}

private fun AccountRepository.createAccount(
    accountId: AccountId,
    name: AccountName,
    email: AccountEmail,
    salt: PasswordSalt,
    cmdId: CmdId = CmdId(abs(Random(10).nextInt()) + 10)
) {
    create(
        CreateCmd(
            id = cmdId,
            accountId = accountId,
            accountType = Enjoyer,
            name = name,
            email = email,
            salt = salt
        )
    )
}

private fun AccountRepository.verifyCount(expected: Int) {
    verifyCount(expected) { }
}

private fun AccountRepository.verifyCount(expected: Int, block: AccountQuery.() -> Unit) {
    val counted = count(AccountQuery().also(block))
    assertThat("number of accounts expected", counted, equalTo(expected.toULong()))
}