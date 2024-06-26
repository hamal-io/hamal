package io.hamal.repository

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.CmdId.Companion.CmdId
import io.hamal.lib.common.domain.Count.Companion.Count
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.domain.Limit.Companion.Limit
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.common.util.TimeUtils.withEpochMilli
import io.hamal.lib.domain.vo.*
import io.hamal.lib.domain.vo.AccountId.Companion.AccountId
import io.hamal.lib.domain.vo.AuthId.Companion.AuthId
import io.hamal.lib.domain.vo.AuthToken.Companion.AuthToken
import io.hamal.lib.domain.vo.Email.Companion.Email
import io.hamal.lib.domain.vo.ExecId.Companion.ExecId
import io.hamal.lib.domain.vo.ExecToken.Companion.ExecToken
import io.hamal.lib.domain.vo.ExpiresAt.Companion.ExpiresAt
import io.hamal.lib.domain.vo.PasswordHash.Companion.PasswordHash
import io.hamal.repository.api.Auth
import io.hamal.repository.api.AuthCmdRepository.*
import io.hamal.repository.api.AuthQueryRepository.AuthQuery
import io.hamal.repository.api.AuthRepository
import io.hamal.repository.fixture.AbstractUnitTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows
import java.time.Instant
import java.time.temporal.ChronoUnit.SECONDS
import kotlin.math.abs
import kotlin.random.Random

internal class AuthRepositoryTest : AbstractUnitTest() {

    @Nested
    inner class CreateTest {
        @TestFactory
        fun `Creates password auth`() = runWith(AuthRepository::class) {
            val result = create(
                CreateEmailAuthCmd(
                    id = CmdId(1),
                    authId = AuthId(2),
                    accountId = AccountId(3),
                    email = Email("email@fn.guru"),
                    hash = PasswordHash("secretPasswordHash")
                )
            )

            with(result) {
                require(this is Auth.Email)
                assertThat(id, equalTo(AuthId(2)))
                assertThat(accountId, equalTo(AccountId(3)))
                assertThat(email, equalTo(Email("email@fn.guru")))
                assertThat(hash, equalTo(PasswordHash("secretPasswordHash")))
            }

            verifyCount(1)
        }

        @TestFactory
        fun `Creates token auth`() = runWith(AuthRepository::class) {
            withEpochMilli(1234567) {
                val result = create(
                    CreateTokenAuthCmd(
                        id = CmdId(1),
                        authId = AuthId(2),
                        accountId = AccountId(3),
                        token = AuthToken("someSuperSecretAccessToken"),
                        expiresAt = ExpiresAt(TimeUtils.now().plus(42, SECONDS))
                    )
                )

                with(result) {
                    require(this is Auth.Token)
                    assertThat(id, equalTo(AuthId(2)))
                    assertThat(accountId, equalTo(AccountId(3)))
                    assertThat(token, equalTo(AuthToken("someSuperSecretAccessToken")))
                    assertThat(expiresAt, equalTo(ExpiresAt(TimeUtils.now().plus(42, SECONDS))))
                }

                verifyCount(1)
            }
        }

        @TestFactory
        fun `Creates exec token auth`() = runWith(AuthRepository::class) {
            withEpochMilli(1234567) {
                val result = create(
                    CreateExecTokenAuthCmd(
                        id = CmdId(1),
                        authId = AuthId(2),
                        execId = ExecId(4),
                        token = ExecToken("this-is-a-one-time-thing"),
                    )
                )

                with(result) {
                    require(this is Auth.ExecToken)
                    assertThat(id, equalTo(AuthId(2)))
                    assertThat(execId, equalTo(ExecId(4)))
                    assertThat(token, equalTo(ExecToken("this-is-a-one-time-thing")))
                }

                verifyCount(1)
            }
        }


        @Nested
        inner class RevokeTest {
            @TestFactory
            fun `Revoke token`() = runWith(AuthRepository::class) {
                createTokenAuth(
                    cmdId = CmdGen(),
                    authId = AuthId(5),
                    accountId = AccountId(3),
                    token = AuthToken("supersecret")
                )

                createEmailAuth(AuthId(1), AccountId(3))
                revokeAuth(RevokeAuthCmd(CmdGen(), AuthId(5)))

                with(list(AccountId(3))) {
                    assertThat(find(AuthToken("supersecret")), nullValue())
                    get(0).also { auth ->
                        require(auth is Auth.Account)
                        assertThat(auth.accountId, equalTo(AccountId(3)))
                    }
                    assertTrue(any { it.id == AuthId(1) })
                    assertFalse(any { it.id == AuthId(5) })
                }
            }

            @TestFactory
            fun `Revokes auth multiple times`() = runWith(AuthRepository::class) {
                createTokenAuth(
                    cmdId = CmdGen(),
                    authId = AuthId(5),
                    accountId = AccountId(3),
                    token = AuthToken("supersecret")
                )

                repeat(10) {
                    revokeAuth(RevokeAuthCmd(CmdGen(), AuthId(5)))
                }

                with(list(AccountId(3))) {
                    assertThat(find(AuthToken("supersecret")), nullValue())
                    assertFalse(any { it.id == AuthId(5) })
                }
            }
        }
    }

    @Nested
    inner class UpdateTest {
        @TestFactory
        fun `Updates Auths hash`() = runWith(AuthRepository::class) {
            create(
                CreateEmailAuthCmd(
                    id = CmdId(1),
                    authId = AuthId(2),
                    accountId = AccountId(3),
                    email = Email("email@fn.guru"),
                    hash = PasswordHash("secretPasswordHash")
                )
            )

            update(
                AuthId(2), UpdatePasswordCmd(
                    id = CmdId(2),
                    hash = PasswordHash("newSecret")
                )
            )

            with(find(AuthId(2))) {
                require(this is Auth.Email)
                assertThat(id, equalTo(AuthId(2)))
                assertThat(accountId, equalTo(AccountId(3)))
                assertThat(email, equalTo(Email("email@fn.guru")))
                assertThat(hash, equalTo(PasswordHash("newSecret")))
            }

            verifyCount(1)
        }
    }

    @Nested
    inner class ClearTest {

        @TestFactory
        fun `Nothing to clear`() = runWith(AuthRepository::class) {
            clear()
            verifyCount(0)
        }

        @TestFactory
        fun `Clear table`() = runWith(AuthRepository::class) {
            createEmailAuth(AuthId(1), AccountId(2))
            createTokenAuth(AuthId(2), AccountId(3), AuthToken("testtoken"))

            clear()
            verifyCount(0)
        }

    }

    @Nested
    inner class GetTest {
        @TestFactory
        fun `Get auth by auth token`() = runWith(AuthRepository::class) {
            withEpochMilli(0) {
                createTokenAuth(
                    authId = AuthId(1),
                    accountId = AccountId(2),
                    token = AuthToken("some-token")
                )

                with(get(AuthToken("some-token"))) {
                    require(this is Auth.Token)
                    assertThat(id, equalTo(AuthId(1)))
                    assertThat(accountId, equalTo(AccountId(2)))
                    assertThat(token, equalTo(AuthToken("some-token")))
                    assertThat(expiresAt, equalTo(ExpiresAt(Instant.ofEpochMilli(123456))))
                }
            }
        }

        @TestFactory
        fun `Tries to get auth by auth token but does not exist`() = runWith(AuthRepository::class) {
            createTokenAuth(
                authId = AuthId(1),
                accountId = AccountId(2),
                token = AuthToken("some-token")
            )


            val exception = assertThrows<NoSuchElementException> {
                get(AuthToken("does-not-exists"))
            }
            assertThat(exception.message, equalTo("Auth not found"))
        }

        @TestFactory
        fun `Get auth by exec token`() = runWith(AuthRepository::class) {
            withEpochMilli(0) {
                createExecTokenAuth(
                    authId = AuthId(1),
                    execId = ExecId(24),
                    token = ExecToken("some-token")
                )

                with(get(ExecToken("some-token"))) {
                    require(this is Auth.ExecToken)
                    assertThat(id, equalTo(AuthId(1)))
                    assertThat(execId, equalTo(ExecId(24)))
                    assertThat(token, equalTo(ExecToken("some-token")))
                }
            }
        }

        @TestFactory
        fun `Tries to get auth by exec token but does not exist`() = runWith(AuthRepository::class) {
            createExecTokenAuth(
                authId = AuthId(1),
                execId = ExecId(24),
                token = ExecToken("some-token")
            )

            assertThrows<NoSuchElementException> { get(ExecToken("does-not-exists")) }
                .also { exception -> assertThat(exception.message, equalTo("Auth not found")) }

        }

        @TestFactory
        fun `Get auth by exec id`() = runWith(AuthRepository::class) {
            withEpochMilli(0) {
                createExecTokenAuth(
                    authId = AuthId(1),
                    execId = ExecId(24),
                    token = ExecToken("some-token")
                )

                with(get(ExecId(24))) {
                    require(this is Auth.ExecToken)
                    assertThat(id, equalTo(AuthId(1)))
                    assertThat(execId, equalTo(ExecId(24)))
                    assertThat(token, equalTo(ExecToken("some-token")))
                }
            }
        }

        @TestFactory
        fun `Tries to get auth by exec id but does not exist`() = runWith(AuthRepository::class) {
            createExecTokenAuth(
                authId = AuthId(1),
                execId = ExecId(24),
                token = ExecToken("some-token")
            )

            assertThrows<NoSuchElementException> { get(ExecId(42)) }
                .also { exception -> assertThat(exception.message, equalTo("Auth not found")) }

        }
    }

    @Nested
    inner class FindTest {

        @TestFactory
        fun `Find auth by token`() = runWith(AuthRepository::class) {
            withEpochMilli(0) {
                createTokenAuth(
                    authId = AuthId(1),
                    accountId = AccountId(2),
                    token = AuthToken("some-token")
                )

                with(find(AuthToken("some-token"))) {
                    require(this is Auth.Token)
                    assertThat(id, equalTo(AuthId(1)))
                    assertThat(accountId, equalTo(AccountId(2)))
                    assertThat(token, equalTo(AuthToken("some-token")))
                    assertThat(expiresAt, equalTo(ExpiresAt(Instant.ofEpochMilli(123456))))
                }
            }
        }

        @TestFactory
        fun `Tries to find auth by auth token, but does not exist`() = runWith(AuthRepository::class) {
            createTokenAuth(
                authId = AuthId(1),
                accountId = AccountId(2),
                token = AuthToken("some-token")
            )

            val result = find(AuthToken("does-not-exists"))
            assertThat(result, nullValue())
        }

        @TestFactory
        fun `Find auth by exec token`() = runWith(AuthRepository::class) {
            withEpochMilli(0) {
                createExecTokenAuth(
                    authId = AuthId(1),
                    execId = ExecId(24),
                    token = ExecToken("some-token")
                )

                with(find(ExecToken("some-token"))) {
                    require(this is Auth.ExecToken)
                    assertThat(id, equalTo(AuthId(1)))
                    assertThat(execId, equalTo(ExecId(24)))
                    assertThat(token, equalTo(ExecToken("some-token")))
                }
            }
        }

        @TestFactory
        fun `Tries to get auth by id by exec token, but does not exist`() = runWith(AuthRepository::class) {
            createExecTokenAuth(
                authId = AuthId(1),
                execId = ExecId(24),
                token = ExecToken("some-token")
            )

            val result = find(ExecToken("does-not-exists"))
            assertThat(result, nullValue())
        }

        @TestFactory
        fun `Find auth by exec id`() = runWith(AuthRepository::class) {
            withEpochMilli(0) {
                createExecTokenAuth(
                    authId = AuthId(1),
                    execId = ExecId(24),
                    token = ExecToken("some-token")
                )

                with(find(ExecId(24))) {
                    require(this is Auth.ExecToken)
                    assertThat(id, equalTo(AuthId(1)))
                    assertThat(execId, equalTo(ExecId(24)))
                    assertThat(token, equalTo(ExecToken("some-token")))
                }
            }
        }

        @TestFactory
        fun `Tries to get auth by id by exec id, but does not exist`() = runWith(AuthRepository::class) {
            createExecTokenAuth(
                authId = AuthId(1),
                execId = ExecId(24),
                token = ExecToken("some-token")
            )

            val result = find(ExecId(42))
            assertThat(result, nullValue())
        }
    }

    @Nested
    inner class ListAndCountTest {

        @TestFactory
        fun `List by account ids`() = runWith(AuthRepository::class) {
            setup()

            val result = list(
                AuthQuery(
                    limit = Limit.all,
                    accountIds = listOf(AccountId(3))
                )
            )
            assertThat(result, hasSize(2))

            with(result[1]) {
                require(this is Auth.Email)
                assertThat(id, equalTo(AuthId(1)))
                assertThat(accountId, equalTo(AccountId(3)))
            }

            with(result[0]) {
                require(this is Auth.Token)
                assertThat(id, equalTo(AuthId(2)))
                assertThat(accountId, equalTo(AccountId(3)))
            }
        }

        @TestFactory
        fun `Tries to list by account id but nothing found`() = runWith(AuthRepository::class) {
            setup()
            val result = list(AccountId(87))
            assertThat(result, empty())
        }

        @TestFactory
        fun `Limit`() = runWith(AuthRepository::class) {
            setup()

            val query = AuthQuery(limit = Limit(3))

            assertThat(count(query), equalTo(Count(5)))
            val result = list(query)
            assertThat(result, hasSize(3))
        }

        @TestFactory
        fun `Skip and limit`() = runWith(AuthRepository::class) {
            setup()

            val query = AuthQuery(
                afterId = AuthId(2),
                limit = Limit(1)
            )

            assertThat(count(query), equalTo(Count(1)))
            val result = list(query)
            assertThat(result, hasSize(1))

            with(result[0]) {
                assertThat(id, equalTo(AuthId(1)))
            }
        }

        private fun AuthRepository.setup() {
            createEmailAuth(
                authId = AuthId(1),
                accountId = AccountId(3)
            )

            createTokenAuth(
                authId = AuthId(2),
                accountId = AccountId(3),
                token = AuthToken("token-two")
            )

            createTokenAuth(
                authId = AuthId(3),
                accountId = AccountId(4),
                token = AuthToken("token-three")
            )

            createTokenAuth(
                authId = AuthId(4),
                accountId = AccountId(10),
                token = AuthToken("token-four")
            )

            createExecTokenAuth(
                authId = AuthId(5),
                execId = ExecId(24),
                token = ExecToken("token-four")
            )
        }
    }
}

private fun AuthRepository.createEmailAuth(
    authId: AuthId,
    accountId: AccountId,
    cmdId: CmdId = CmdId(abs(Random(10).nextInt()) + 10)
) {
    create(
        CreateEmailAuthCmd(
            id = cmdId,
            authId = authId,
            accountId = accountId,
            email = Email("some@email.com"),
            hash = PasswordHash("SomeHash")
        )
    )
}

private fun AuthRepository.createTokenAuth(
    authId: AuthId,
    accountId: AccountId,
    token: AuthToken,
    cmdId: CmdId = CmdId(abs(Random(10).nextInt()) + 10)
) {
    create(
        CreateTokenAuthCmd(
            id = cmdId,
            authId = authId,
            accountId = accountId,
            token = token,
            expiresAt = ExpiresAt(TimeUtils.now().plusMillis(123456))
        )
    )
}

private fun AuthRepository.createExecTokenAuth(
    authId: AuthId,
    execId: ExecId,
    token: ExecToken,
    cmdId: CmdId = CmdId(abs(Random(10).nextInt()) + 10)
) {
    create(
        CreateExecTokenAuthCmd(
            id = cmdId,
            authId = authId,
            execId = execId,
            token = token
        )
    )
}


private fun AuthRepository.verifyCount(expected: Int) {
    verifyCount(expected) { }
}

private fun AuthRepository.verifyCount(expected: Int, block: AuthQuery.() -> Unit) {
    val counted = count(AuthQuery().also(block))
    assertThat("number of auths expected", counted, equalTo(Count(expected)))
}