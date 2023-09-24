package io.hamal.repository.sqlite

import io.hamal.lib.domain.vo.AuthToken
import io.hamal.lib.sqlite.BaseSqliteRepository
import io.hamal.repository.api.Auth
import io.hamal.repository.api.AuthCmdRepository
import io.hamal.repository.api.AuthQueryRepository
import io.hamal.repository.api.AuthRepository
import java.nio.file.Path

class SqliteAuthRepository(
    config: Config
) : AuthRepository {
    data class Config(
        override val path: Path
    ) : BaseSqliteRepository.Config {
        override val filename = "auth.db"
    }

    override fun create(cmd: AuthCmdRepository.CreateCmd): Auth {
        TODO("Not yet implemented")
    }

    override fun list(query: AuthQueryRepository.AuthQuery): List<Auth> {
        TODO("Not yet implemented")
    }

    override fun count(query: AuthQueryRepository.AuthQuery): ULong {
        TODO("Not yet implemented")
    }

    override fun clear() {

    }

    override fun close() {

    }

    override fun find(authToken: AuthToken): Auth? {
        TODO("Not yet implemented")
    }


}