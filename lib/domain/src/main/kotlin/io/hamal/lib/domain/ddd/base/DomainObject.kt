package io.hamal.lib.domain.ddd.base

import io.hamal.lib.domain.Shard
import io.hamal.lib.domain.vo.base.DomainId

interface DomainObject<ID : DomainId> {
    val id: ID
    val shard get(): Shard = Shard(id.partition().value.toInt())
}