package io.hamal.backend.repository.api

import io.hamal.lib.common.Partition

interface CmdRepository {
    val partition: Partition
}

interface QueryRepository {
    val partition: Partition
}