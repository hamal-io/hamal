package io.hamal.lib.domain

import io.hamal.lib.domain.vo.base.DomainId


interface DomainObject<ID : DomainId> {
    val id: ID
    val shard get() = id.shard()
}