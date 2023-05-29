package io.hamal.backend.repository.api

import io.hamal.lib.common.Shard

interface CmdRepository {
    val shard: Shard
}

interface QueryRepository {
    val shard: Shard
}