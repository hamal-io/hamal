package io.hamal.repository.memory

import io.hamal.lib.domain.vo.AccountId
import io.hamal.repository.api.AuthRepository
import io.hamal.repository.api.Auth
import io.hamal.repository.api.AuthCmdRepository.CreateCmd
import io.hamal.repository.api.AuthCmdRepository.CreatePasswordAuthCmd
import io.hamal.repository.api.PasswordAuth
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
                    salt = cmd.salt
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
}