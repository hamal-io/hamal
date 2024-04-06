package io.hamal.repository.memory

import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.Auth
import io.hamal.repository.api.AuthCmdRepository.*
import io.hamal.repository.api.AuthQueryRepository.AuthQuery
import io.hamal.repository.api.AuthRepository
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

class AuthMemoryRepository : AuthRepository {

    override fun create(cmd: CreateCmd): Auth {
        return lock.write {
            when (cmd) {
                is CreateEmailAuthCmd -> Auth.Email(
                    cmdId = cmd.id,
                    id = cmd.authId,
                    accountId = cmd.accountId,
                    email = cmd.email,
                    hash = cmd.hash,
                ).also { projections.add(it) }

                is CreateMetaMaskAuthCmd -> Auth.MetaMask(
                    cmdId = cmd.id,
                    id = cmd.authId,
                    accountId = cmd.accountId,
                    address = cmd.address
                ).also { projections.add(it) }

                is CreateTokenAuthCmd -> Auth.Token(
                    cmdId = cmd.id,
                    id = cmd.authId,
                    accountId = cmd.accountId,
                    token = cmd.token,
                    expiresAt = cmd.expiresAt
                ).also { projections.add(it) }

                is CreateExecTokenAuthCmd -> Auth.ExecToken(
                    cmdId = cmd.id,
                    id = cmd.authId,
                    token = cmd.token,
                    execId = cmd.execId
                ).also { projections.add(it) }
            }
        }
    }

    override fun revokeAuth(cmd: RevokeAuthCmd) {
        return lock.write {
            projections.removeIf { it.id == cmd.authId }
        }
    }


    override fun list(query: AuthQuery): List<Auth> {
        return projections
            .filter { auth ->
                if (query.accountIds.isEmpty()) true else {
                    if (auth is Auth.Account) auth.accountId in query.accountIds else false
                }
            }
            .reversed()
            .asSequence()
            .filter { if (query.authIds.isEmpty()) true else query.authIds.contains(it.id) }
            .dropWhile { it.id >= query.afterId }
            .take(query.limit.value)
            .toList()
    }

    override fun count(query: AuthQuery): Count {
        return Count(
            projections
                .filter { auth ->
                    if (query.accountIds.isEmpty()) true else {
                        if (auth is Auth.Account) auth.accountId in query.accountIds else false
                    }
                }
                .reversed()
                .asSequence()
                .filter { if (query.authIds.isEmpty()) true else query.authIds.contains(it.id) }
                .dropWhile { it.id >= query.afterId }
                .count()
                .toLong()
        )
    }

    override fun clear() {
        lock.write { projections.clear() }
    }

    override fun close() {}

    override fun find(authId: AuthId): Auth? {
        return lock.read {
            projections.find { it.id == authId }
        }
    }

    override fun find(execId: ExecId): Auth.ExecToken? {
        return lock.read {
            projections.filterIsInstance<Auth.ExecToken>().find { it.execId == execId }
        }
    }

    override fun find(authToken: AuthToken): Auth? {
        return lock.read {
            projections.asSequence()
                .filterIsInstance<Auth.Token>()
                .find { it.token == authToken }
        }
    }

    override fun find(execToken: ExecToken): Auth.ExecToken? {
        return lock.read {
            projections.asSequence()
                .filterIsInstance<Auth.ExecToken>()
                .find { it.token == execToken }
        }
    }

    override fun find(email: Email): Auth? {
        return lock.read {
            projections.asSequence()
                .filterIsInstance<Auth.Email>()
                .find { it.email == email }
        }
    }

    override fun find(address: Web3Address): Auth? {
        return lock.read {
            projections.asSequence()
                .filterIsInstance<Auth.MetaMask>()
                .find { it.address == address }
        }
    }

    override fun update(authId: AuthId, cmd: UpdateEmailHashCmd): Auth {
        val currentAuth = find(authId) as? Auth.Email
        val updatedAuth = currentAuth?.copy(
            cmdId = cmd.id,
            accountId = currentAuth.accountId,
            email = currentAuth.email,
            hash = cmd.hash
        )

        lock.write {
            projections.removeIf { it.id == currentAuth?.id }
            updatedAuth?.let { projections.add(it) }
        }

        return updatedAuth as Auth
    }

    private val lock = ReentrantReadWriteLock()
    private val projections = mutableListOf<Auth>()

}