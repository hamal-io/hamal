package io.hamal.lib.ddd.base

import io.hamal.lib.Shard
import io.hamal.lib.vo.base.DomainId

interface DomainObject<ID : DomainId> {
    val id: ID
    val shard get(): Shard = Shard(id.partition().value.toInt())
}