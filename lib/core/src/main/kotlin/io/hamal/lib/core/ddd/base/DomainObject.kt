package io.hamal.lib.core.ddd.base

import io.hamal.lib.core.Shard
import io.hamal.lib.core.vo.base.DomainId

interface DomainObject<ID : DomainId> {
    val id: ID
    val shard get(): Shard = Shard(id.partition().value.toInt())
}