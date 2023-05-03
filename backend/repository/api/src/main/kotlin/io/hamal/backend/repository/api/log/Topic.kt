package io.hamal.backend.repository.api.log

import io.hamal.lib.Shard
import java.nio.file.Path

data class Topic(
    val id: Id,
    val brokerId: Broker.Id,
    val name: Name,
    val path: Path,
    val shard: Shard
) {

    @JvmInline
    value class Name(val value: String)

    @JvmInline
    value class Id(val value: ULong) {
        constructor(value: Int) : this(value.toULong())
    }
}
