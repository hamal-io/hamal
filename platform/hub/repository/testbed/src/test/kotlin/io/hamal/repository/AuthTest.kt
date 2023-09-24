package io.hamal.repository

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.common.util.TimeUtils.withEpochMilli
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.AuthCmdRepository.CreatePasswordAuthCmd
import io.hamal.repository.api.AuthCmdRepository.CreateTokenAuthCmd
import io.hamal.repository.api.AuthQueryRepository.AuthQuery
import io.hamal.repository.api.AuthRepository
import io.hamal.repository.api.PasswordAuth
import io.hamal.repository.api.TokenAuth
import io.hamal.repository.fixture.AbstractUnitTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
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
                CreatePasswordAuthCmd(
                    id = CmdId(1),
                    authId = AuthId(2),
                    accountId = AccountId(3),
                    hash = PasswordHash("secretPasswordHash")
                )
            )

            with(result) {
                require(this is PasswordAuth)
                assertThat(id, equalTo(AuthId(2)))
                assertThat(accountId, equalTo(AccountId(3)))
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
                        expiresAt = AuthTokenExpiresAt(TimeUtils.now().plus(42, SECONDS))
                    )
                )

                with(result) {
                    require(this is TokenAuth)
                    assertThat(id, equalTo(AuthId(2)))
                    assertThat(accountId, equalTo(AccountId(3)))
                    assertThat(token, equalTo(AuthToken("someSuperSecretAccessToken")))
                    assertThat(expiresAt, equalTo(AuthTokenExpiresAt(TimeUtils.now().plus(42, SECONDS))))
                }

                verifyCount(1)
            }
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
            createPasswordAuth(AuthId(1), AccountId(2))
            createTokenAuth(AuthId(2), AccountId(3), AuthToken("testtoken"))

            clear()
            verifyCount(0)
        }

    }

    @Nested
    inner class GetTest {
        @TestFactory
        fun `Get auth by token`() = runWith(AuthRepository::class) {
            withEpochMilli(0) {
                createTokenAuth(
                    authId = AuthId(1),
                    accountId = AccountId(2),
                    token = AuthToken("some-token")
                )

                with(get(AuthToken("some-token"))) {
                    require(this is TokenAuth)
                    assertThat(id, equalTo(AuthId(1)))
                    assertThat(accountId, equalTo(AccountId(2)))
                    assertThat(token, equalTo(AuthToken("some-token")))
                    assertThat(expiresAt, equalTo(AuthTokenExpiresAt(Instant.ofEpochMilli(123456))))
                }
            }
        }

        @TestFactory
        fun `Tries to get auth by id but does not exist`() = runWith(AuthRepository::class) {
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
                    require(this is TokenAuth)
                    assertThat(id, equalTo(AuthId(1)))
                    assertThat(accountId, equalTo(AccountId(2)))
                    assertThat(token, equalTo(AuthToken("some-token")))
                    assertThat(expiresAt, equalTo(AuthTokenExpiresAt(Instant.ofEpochMilli(123456))))
                }
            }
        }

        @TestFactory
        fun `Tries to get auth by id but does not exist`() = runWith(AuthRepository::class) {
            createTokenAuth(
                authId = AuthId(1),
                accountId = AccountId(2),
                token = AuthToken("some-token")
            )

            val result = get(AuthToken("does-not-exists"))
            assertThat(result, nullValue())
        }
    }

    @Nested
    inner class ListAndCountTest {

        @TestFactory
        fun `List by accountId`() = runWith(AuthRepository::class) {
            setup()

            val result = list(AccountId(3))
            assertThat(result, hasSize(2))

            with(result[1]) {
                require(this is PasswordAuth)
                assertThat(id, equalTo(AuthId(1)))
                assertThat(accountId, equalTo(AccountId(3)))
            }

            with(result[0]) {
                require(this is TokenAuth)
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

            assertThat(count(query), equalTo(4UL))
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

            assertThat(count(query), equalTo(1UL))
            val result = list(query)
            assertThat(result, hasSize(1))

            with(result[0]) {
                assertThat(id, equalTo(AuthId(1)))
            }
        }

        private fun AuthRepository.setup() {
            createPasswordAuth(
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
        }
    }
}

private fun AuthRepository.createPasswordAuth(
    authId: AuthId,
    accountId: AccountId,
    cmdId: CmdId = CmdId(abs(Random(10).nextInt()) + 10)
) {
    create(
        CreatePasswordAuthCmd(
            id = cmdId,
            authId = authId,
            accountId = accountId,
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
            expiresAt = AuthTokenExpiresAt(TimeUtils.now().plusMillis(123456))
        )
    )
}

private fun AuthRepository.verifyCount(expected: Int) {
    verifyCount(expected) { }
}

private fun AuthRepository.verifyCount(expected: Int, block: AuthQuery.() -> Unit) {
    val counted = count(AuthQuery().also(block))
    assertThat("number of auths expected", counted, equalTo(expected.toULong()))
}