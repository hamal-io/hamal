package io.hamal.repository.memory

import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.AuthToken
import io.hamal.repository.api.*
import io.hamal.repository.api.AuthCmdRepository.*
import io.hamal.repository.api.AuthQueryRepository.AuthQuery
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

class AuthMemoryRepository : AuthRepository {
    private val lock = ReentrantReadWriteLock()

    private val projection = mutableMapOf<AccountId, MutableList<Auth>>()

    override fun create(cmd: CreateCmd): Auth {
        return lock.write {
            when (cmd) {
                is CreatePasswordAuthCmd -> PasswordAuth(
                    cmdId = cmd.id,
                    id = cmd.authId,
                    accountId = cmd.accountId,
                    hash = cmd.hash,
                ).also {
                    projection.putIfAbsent(it.accountId, mutableListOf())
                    projection[it.accountId]!!.add(it)
                }

                is CreateMetaMaskAuthCmd -> MetaMaskAuth(
                    cmdId = cmd.id,
                    id = cmd.authId,
                    accountId = cmd.accountId,
                    address = cmd.address
                ).also {
                    projection.putIfAbsent(it.accountId, mutableListOf())
                    projection[it.accountId]!!.add(it)
                }

                is CreateTokenAuthCmd -> TokenAuth(
                    cmdId = cmd.id,
                    id = cmd.authId,
                    accountId = cmd.accountId,
                    token = cmd.token,
                    expiresAt = cmd.expiresAt
                ).also {
                    projection.putIfAbsent(it.accountId, mutableListOf())
                    projection[it.accountId]!!.add(it)
                }
            }
        }
    }

    override fun revokeAuth(cmd: RevokeAuthCmd) {
        return lock.write {
            projection.values.forEach {
                it.removeIf { it.id == cmd.authId }
            }
        }
    }


    override fun list(query: AuthQuery): List<Auth> {
        return projection.filter { query.accountIds.isEmpty() || it.key in query.accountIds }
            .flatMap { it.value }
            .reversed()
            .asSequence()
            .filter {
                if (query.authIds.isEmpty()) true else query.authIds.contains(it.id)
            }.dropWhile { it.id >= query.afterId }
            .take(query.limit.value)
            .toList()
    }

    override fun count(query: AuthQuery): ULong {
        return projection.filter { query.accountIds.isEmpty() || it.key in query.accountIds }
            .flatMap { it.value }
            .reversed()
            .asSequence()
            .filter {
                if (query.authIds.isEmpty()) true else query.authIds.contains(it.id)
            }.dropWhile { it.id >= query.afterId }
            .count()
            .toULong()
    }

    override fun clear() {
        lock.write { projection.clear() }
    }

    override fun close() {}

    override fun find(authToken: AuthToken): Auth? {
        return lock.read {
            projection.flatMap { it.value }
                .asSequence()
                .filterIsInstance<TokenAuth>()
                .find { it.token == authToken }
        }
    }
}