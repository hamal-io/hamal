package io.hamal.repository.memory

import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.AuthToken
import io.hamal.repository.api.Auth
import io.hamal.repository.api.AuthCmdRepository.*
import io.hamal.repository.api.AuthRepository
import io.hamal.repository.api.PasswordAuth
import io.hamal.repository.api.TokenAuth
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

object MemoryAuthRepository : AuthRepository {
    private val lock = ReentrantReadWriteLock()

    private val store = mutableMapOf<AccountId, MutableList<Auth>>()

    override fun create(cmd: CreateCmd): Auth {
        return lock.write {
            when (cmd) {
                is CreatePasswordAuthCmd -> PasswordAuth(
                    cmdId = cmd.id,
                    id = cmd.authId,
                    accountId = cmd.accountId,
                    hash = cmd.hash,
                ).also {
                    store.putIfAbsent(it.accountId, mutableListOf())
                    store[it.accountId]!!.add(it)
                }

                is CreateTokenAuthCmd -> TokenAuth(
                    cmdId = cmd.id,
                    id = cmd.authId,
                    accountId = cmd.accountId,
                    token = cmd.token,
                    expiresAt = cmd.expiresAt
                ).also {
                    store.putIfAbsent(it.accountId, mutableListOf())
                    store[it.accountId]!!.add(it)
                }
            }
        }
    }

    override fun list(accountId: AccountId): List<Auth> {
        return lock.read { store[accountId] ?: listOf() }
    }

    override fun clear() {
        lock.write { store.clear() }
    }

    override fun find(authToken: AuthToken): Auth? {
        return lock.read {
            store.flatMap { it.value }
                .asSequence()
                .filterIsInstance<TokenAuth>()
                .find { it.token == authToken }
        }
    }
}